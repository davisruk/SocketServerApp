package uk.co.boots.dsp.api.gs1;

import lombok.Data;

@Data
public class GSOneBarcode {
	private String barcode;
	private String gtin;
	private String batchNumber;
	private String expiryDate;
	private String serialNumber;
	
	public GSOneBarcode() {
		this.barcode = "Not Present";
		this.gtin = "Not Present";
		this.batchNumber = "Not Present";
		this.expiryDate = "Not Present";
		this.serialNumber = "Not Present";
	}
}
