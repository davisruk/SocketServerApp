package uk.co.boots.dsp.wcs.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.co.boots.dsp.wcs.masterdata.BarcodeAssociation;

@Repository
public interface BarcodeMasterDataRepository extends CrudRepository<BarcodeAssociation, Long>{

}
