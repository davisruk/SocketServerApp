package uk.co.boots.dsp.wcs.masterdata;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ProductMasterDataList {
	private List<ProductMasterData> lines = new ArrayList<ProductMasterData>();
}
