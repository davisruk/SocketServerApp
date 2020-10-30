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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import uk.co.boots.dsp.api.dto.MessageDTO;
import uk.co.boots.dsp.api.dto.PageRequestDetail;
import uk.co.boots.dsp.api.dto.ToteDTOService;
import uk.co.boots.dsp.api.dto.ToteMessageSummary;
import uk.co.boots.dsp.api.dto.ToteSummaryPage;
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

	@PostMapping("/prettify")
    public Tote prettifyMessage(@RequestParam("file") MultipartFile file) throws IOException{
		return getToteFromBytes(file.getBytes());
    }

	@GetMapping("/tote/messages/prettify")
    public Tote prettifyMessage(@RequestParam long messageId) throws IOException{
		// get the raw message
		RawMessage rm = toteService.getRawMessage(messageId);
		return getToteFromBytes(rm.getMessage().getBytes());
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
		messageBytes = buf.toByteArray();		
		String msgType = new String(messageBytes, messageTypePos, messageTypeLength);
		Deserializer d = deserializerFactory.getDeserializer(msgType).get();
		return (Tote) d.deserialize(messageBytes);
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
