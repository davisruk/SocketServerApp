package uk.co.boots.dsp.wcs.masterdata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@Entity
@JsonInclude(Include.NON_NULL)
public class BarcodeAssociation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Long Id;
	private String productId;
	private String barcode;
}
