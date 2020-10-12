package uk.co.boots.dsp.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import uk.co.boots.dsp.messages.Deserializer;
import uk.co.boots.dsp.messages.DeserializerFactory;
import uk.co.boots.dsp.messages.shared.Tote;
import uk.co.boots.dsp.wcs.masterdata.ProductMasterDataList;
import uk.co.boots.dsp.wcs.rules.RuleParameterList;
import uk.co.boots.dsp.wcs.service.MasterDataService;

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
	MasterDataService masterDataService;

	@PostMapping("/prettify")
    public Tote prettifyMessage(@RequestParam("file") MultipartFile file) throws IOException{
		byte[] messageBytes = file.getBytes();
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		if (messageBytes[0] != 0x0A) {
			buf.write(0x0A);
		}
		buf.write(messageBytes, 0, messageBytes.length - 1);
		if (messageBytes[messageBytes.length-1] != 0x0D) {
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
			if ("barcodes.txt".equals(fileName))
				bcl = masterDataService.translateBarcodes(file);
			if ("rules.txt".equals(fileName))
				rpl = masterDataService.translateRules(file);
		}
		
		masterDataService.saveBarcodes(bcl);
		masterDataService.saveRules(rpl);
		
		return new ResponseEntity<>("Files received and persisted", HttpStatus.OK);
	}
	

}
