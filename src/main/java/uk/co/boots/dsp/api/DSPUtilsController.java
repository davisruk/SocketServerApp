package uk.co.boots.dsp.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import uk.co.boots.dsp.api.dto.MessageDTO;
import uk.co.boots.dsp.api.dto.PageRequestDetail;
import uk.co.boots.dsp.api.dto.ToteDTOService;
import uk.co.boots.dsp.api.dto.ToteMessageSummary;
import uk.co.boots.dsp.api.dto.ToteSummaryPage;
import uk.co.boots.dsp.api.gs1.GS1Builder;
import uk.co.boots.dsp.api.gs1.GSOneBarcode;
import uk.co.boots.dsp.comms.tcp.SocketServer;
import uk.co.boots.dsp.messages.base.entity.RawMessage;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.messages.framework.serialization.Deserializer;
import uk.co.boots.dsp.messages.framework.serialization.DeserializerFactory;
import uk.co.boots.dsp.wcs.masterdata.entity.ProductMasterDataList;
import uk.co.boots.dsp.wcs.masterdata.service.MasterDataService;
import uk.co.boots.dsp.wcs.rules.RuleParameterList;
import uk.co.boots.dsp.wcs.service.ToteService;

@RestController
@RequestMapping("/utils")
public class DSPUtilsController {

	@Value("${message_type_offset}")
	private int messageTypePos;
	
	@Value("${message_type_length}")
	private int messageTypeLength;
	
	@Autowired
	private DeserializerFactory deserializerFactory;
	
	@Autowired
	private MasterDataService masterDataService;
	@Autowired
	private ToteDTOService toteDTOService;

	@Autowired
	private ToteService toteService;

	private final ObjectMapper objectMapper;
	
	@Autowired
	public DSPUtilsController(DeserializerFactory deserializerFactory, MasterDataService masterDataService,
			ToteDTOService toteDTOService, ToteService toteService, ObjectMapper mapper) {
		this.deserializerFactory = deserializerFactory;
		this.masterDataService = masterDataService;
		this.toteDTOService = toteDTOService;
		this.toteService = toteService;
		this.objectMapper = mapper.copy().enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	@PostMapping("/prettify")
    public Tote prettifyMessage(@RequestParam("file") MultipartFile file) throws IOException{
		return getToteFromBytes(file.getBytes());
    }
	
	public static class PrettifyRequest {
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	@PostMapping(path="/prettifyMessage",
				produces = MediaType.APPLICATION_JSON_VALUE,
				consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> prettifyMessage(@RequestBody PrettifyRequest req) throws IOException{
		String message = req.getMessage();
		Tote t  = getToteFromBytes(message.getBytes());
		GS1Builder gs1Builder = new GS1Builder();
		String json = objectMapper
				.writerWithDefaultPrettyPrinter()
				.writeValueAsString(t);
		
		// Unpack the GS1 barcodes to JSON objects
		// and replace the barcode strings with the GSOneBarcode objects
		// Really this should be done in the deserializer,
		// but the OrderLine->GSOneDetail structure only
		// contains the barcode string, not the GSOneBarcode object
		String retVal = updateGsOneFields(json, gs1Builder);

		return ResponseEntity
			.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(retVal);
    }
	
	@Data
	public static class GSOneBarcodeRequest {
		private String barcode;
	}
	
	@PostMapping(path="/prettifyGS1",
				produces = MediaType.APPLICATION_JSON_VALUE,
				consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> prettifyGS1(@RequestBody GSOneBarcodeRequest request) throws IOException{
		GS1Builder gs1Builder = new GS1Builder();
		GSOneBarcode barcode = gs1Builder.createGSOneFromBarcodeString(request.getBarcode());
		ObjectMapper objectMapper = new ObjectMapper();
		String retVal = objectMapper.writerWithDefaultPrettyPrinter()
		.writeValueAsString(barcode);
		
		return ResponseEntity
			.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(retVal);
	}
	
	private String updateGsOneFields(String originalJson, GS1Builder builder) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(originalJson);
		replaceGsOneFields(rootNode, builder, objectMapper);
		return objectMapper.writeValueAsString(rootNode);
	}

	private void replaceGsOneFields(JsonNode node, GS1Builder builder, ObjectMapper objectMapper) {
		if (node.isObject()) {
			ObjectNode objectNode = (ObjectNode) node;
			if (objectNode.has("gsOne")) {
				String gsOneValue = objectNode.get("gsOne").asText();
				GSOneBarcode gs1 = builder.createGSOneFromBarcodeString(gsOneValue);
				JsonNode gs1Node = objectMapper.valueToTree(gs1);
				objectNode.set("gsOne", gs1Node);
			}
			objectNode.fields().forEachRemaining(entry -> {
				replaceGsOneFields(entry.getValue(), builder, objectMapper);
			});
		} else if (node.isArray()) {
			for (JsonNode arrayElement : node) {
				replaceGsOneFields(arrayElement, builder, objectMapper);
			}
		}
	}
	
	@GetMapping("/tote/messages/{id}")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable("id") long messageId) throws IOException{
		// get the raw message
		RawMessage rm = toteService.getRawMessage(messageId);
		Tote t = getToteFromBytes(rm.getMessage().getBytes());
		return ResponseEntity.ok(new MessageDTO(rm.getMessage(), t)); 
    }

	private Tote getToteFromBytes(byte[] messageBytes) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		if (messageBytes[0] != SocketServer.START_FRAME) {
			buf.write(0x0A);
		}
		buf.write(messageBytes, 0, messageBytes.length);
		if (messageBytes[messageBytes.length-1] != SocketServer.END_FRAME) {
			buf.write(0x0D);
		}
		
		// write the message length into the message if required
		messageBytes = insertLengthIfMissing(buf.toByteArray());		
		String msgType = new String(messageBytes, messageTypePos, messageTypeLength);
		Deserializer d = deserializerFactory.getDeserializer(msgType).get();
		return (Tote) d.deserialize(messageBytes);
	}
	
	// note - you must only call this method once the message contains start and end frame bytes
	// length field is 5 bytes long
	// if the length is missing we are guaranteed to have a message type somewhere in those 5 bytes
	// message type always contains a letter and so cannot be a number
	private byte[] insertLengthIfMissing (byte[] payload) {
		final int lengthFieldSize = 5;
		if (isAsciiDigitBlock(payload, 1, lengthFieldSize)) {
			return payload;
		}
		
		byte[] buff = new byte[payload.length + lengthFieldSize];
		//Start frame
		System.arraycopy(payload, 0, buff, 0, 1);
		//End frame
		System.arraycopy(payload, payload.length - 1, buff, buff.length - 1, 1);
		// Payload with length
		int contentLength = payload.length - 2; // -2 for start and end frame
		String payloadLength = String.format("%05d", contentLength);
		byte[] lengthBytes = payloadLength.getBytes();
		System.arraycopy(lengthBytes, 0, buff, 1, lengthFieldSize);
		System.arraycopy(payload, 1, buff, lengthFieldSize + 1, contentLength); // +1 for start frame
		return buff;
	}
	
	private boolean isAsciiDigitBlock(byte[] data, int offset, int length) {
		for (int i = offset; i < offset + length; i++) {
			byte b = data[offset + i];
			if (b < '0' || b > '9') {
				return false;
			}
		}
		return true;
	}
	
	@RequestMapping (path="/uploadBarcodes",  method=RequestMethod.POST, consumes = {"multipart/form-data"})
	public ResponseEntity<String> uploadBarcodes(@RequestParam("file") MultipartFile file) throws IOException{
		ProductMasterDataList l = masterDataService.translateBarcodes(file);
		masterDataService.saveBarcodes(l);
		return new ResponseEntity<>("File received and persisted", HttpStatus.OK);
	}
	
	@RequestMapping (path="/uploadRules",  method=RequestMethod.POST, consumes = {"multipart/form-data"})
	public ResponseEntity<String> uploadRules(@RequestParam("file") MultipartFile file) throws IOException{
		RuleParameterList l = masterDataService.translateRules(file);
		masterDataService.saveRules(l);
		return new ResponseEntity<>("File received and persisted", HttpStatus.OK);
	}

	@PostMapping(value="/uploadMasterData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMasterData(@RequestParam("files") MultipartFile[] files) throws IOException{
		ProductMasterDataList bcl = null;
		RuleParameterList rpl = null;
		
		for(MultipartFile file: files) {
			String fileName = file.getOriginalFilename();
			if (fileName.toLowerCase().contains("products"))
				bcl = masterDataService.translateBarcodes(file);
			if (fileName.toLowerCase().contains("rules"))
				rpl = masterDataService.translateRules(file);
		}
		
		masterDataService.saveBarcodes(bcl);
		masterDataService.saveRules(rpl);
		
		return new ResponseEntity<>("Files received and persisted", HttpStatus.OK);
	}
	
	@GetMapping(path = "/tote/page")
	public ResponseEntity<ToteSummaryPage> loadTotePage(@RequestParam int pageNumber, @RequestParam int pageSize,
			@RequestParam String filter) {
		ToteSummaryPage result = toteDTOService
				.getSummaryDTOsForPage(new PageRequestDetail(pageNumber, pageSize, filter));
		return ResponseEntity.ok().body(result);
	}

	@GetMapping(path = "/tote/messages")
	public ResponseEntity<ToteMessageSummary> loadToteMessages(@RequestParam long toteId) {
		ToteMessageSummary result = toteDTOService.getMessageDTOsForTote(toteId);
		return ResponseEntity.ok().body(result);
	}
}
