package uk.co.boots.dsp.wcs.masterdata.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.boots.dsp.wcs.masterdata.entity.ProductMasterData;
import uk.co.boots.dsp.wcs.masterdata.entity.ProductMasterDataList;
import uk.co.boots.dsp.wcs.masterdata.repository.ProductMasterDataRepository;
import uk.co.boots.dsp.wcs.masterdata.repository.TestRuleMasterDataRepository;
import uk.co.boots.dsp.wcs.rules.RuleParameterList;
import uk.co.boots.dsp.wcs.rules.RuleParameters;

@Service
public class MasterDataService {

	@Autowired
	private ProductMasterDataRepository barcodeMasterDataRepository;
	
	@Autowired
	private TestRuleMasterDataRepository testRuleMasterDataRepository;
	
	
	public void saveBarcodes (ProductMasterDataList list) {
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
	
	public ProductMasterDataList translateBarcodes (MultipartFile file) throws IOException {
        //read json file and convert to barcode associations
        return new ObjectMapper().readValue(file.getInputStream(), ProductMasterDataList.class);
	}
	
	public RuleParameterList translateRules (MultipartFile file) throws IOException {
        //read json file and convert to barcode associations
        return new ObjectMapper().readValue(file.getInputStream(), RuleParameterList.class);
	}
	
	public Optional<ProductMasterData> getInfoForProduct (String productId) {
		Long dppId = Long.valueOf(productId.trim());
		ProductMasterData ba = barcodeMasterDataRepository.findFirstByDppId(dppId);
		return Optional.ofNullable(ba); 
	}

	public Optional<List<RuleParameters>> getRulesForProduct (String productId) {
		List<RuleParameters> l = testRuleMasterDataRepository.findByProductCode(productId.trim());
		return Optional.ofNullable(l); 
	}
	
}
