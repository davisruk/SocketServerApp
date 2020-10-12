package uk.co.boots.dsp.wcs.masterdata;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BarcodeAssociationList {
	private List<BarcodeAssociation> lines = new ArrayList<BarcodeAssociation>();
}
