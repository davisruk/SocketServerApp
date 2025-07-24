package uk.co.boots.dsp.wcs.masterdata.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
