package uk.co.boots.dsp.wcs.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.boots.dsp.wcs.masterdata.BarcodeAssociation;
import uk.co.boots.dsp.wcs.masterdata.BarcodeAssociationList;
import uk.co.boots.dsp.wcs.repository.BarcodeMasterDataRepository;
import uk.co.boots.dsp.wcs.repository.TestRuleMasterDataRepository;
import uk.co.boots.dsp.wcs.rules.RuleParameterList;

@Service
public class MasterDataService {

	@Autowired
	private BarcodeMasterDataRepository barcodeMasterDataRepository;
	
	@Autowired
	private TestRuleMasterDataRepository testRuleMasterDataRepository;
	
	
	public void saveBarcodes (BarcodeAssociationList list) {
		if (list != null) {
			barcodeMasterDataRepository.deleteAll();
			list.getLines().forEach(barcode -> barcodeMasterDataRepository.save(barcode));
		}
	}
	
	public void saveRules (RuleParameterList list) {
		if (list != null) {
			testRuleMasterDataRepository.deleteAll();
			list.getLines().forEach(rule -> testRuleMasterDataRepository.save(rule));
		}
	}
	
	public BarcodeAssociationList translateBarcodes (MultipartFile file) throws IOException {
        //read json file and convert to barcode associations
        return new ObjectMapper().readValue(file.getInputStream(), BarcodeAssociationList.class);
	}
	
	public RuleParameterList translateRules (MultipartFile file) throws IOException {
        //read json file and convert to barcode associations
        return new ObjectMapper().readValue(file.getInputStream(), RuleParameterList.class);
	}
	
	public Optional<BarcodeAssociation> getBarcodeForProduct (String productId) {
		BarcodeAssociation ba = barcodeMasterDataRepository.findByProductId(productId.trim());
		return Optional.ofNullable(ba); 
	}
	
}
