package uk.co.boots.dsp.wcs.masterdata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Entity
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "Id" })
public class ProductMasterData {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	@JsonProperty("DPPId")
	private Long dppId;
	@JsonProperty("PackSize")
	private int packSize;
	@JsonProperty("EANBarcode")
	private String eanBarcode;
	@JsonProperty("GTIN")
	private String gtin;
}
