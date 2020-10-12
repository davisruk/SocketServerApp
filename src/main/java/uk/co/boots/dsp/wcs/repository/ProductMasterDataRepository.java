package uk.co.boots.dsp.wcs.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.co.boots.dsp.wcs.masterdata.ProductMasterData;

@Repository
public interface ProductMasterDataRepository extends CrudRepository<ProductMasterData, Long>{
	public ProductMasterData findFirstByDppId(Long dppId);
}
