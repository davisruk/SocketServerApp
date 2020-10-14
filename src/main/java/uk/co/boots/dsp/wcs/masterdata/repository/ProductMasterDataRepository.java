package uk.co.boots.dsp.wcs.masterdata.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.co.boots.dsp.wcs.masterdata.entity.ProductMasterData;

@Repository
public interface ProductMasterDataRepository extends CrudRepository<ProductMasterData, Long>{
	public ProductMasterData findFirstByDppId(Long dppId);
}
