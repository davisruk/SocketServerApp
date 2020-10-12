package uk.co.boots.dsp.wcs.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import uk.co.boots.dsp.wcs.rules.RuleParameters;

public interface TestRuleMasterDataRepository  extends CrudRepository<RuleParameters, Long>{

	public List<RuleParameters> findByProductCode(String productCode);
	
}
