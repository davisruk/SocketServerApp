package uk.co.boots.dsp.api.gs1;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import uk.co.boots.dsp.api.gs1.GS1BuilderParameters.GS1BuildParameter;

@Service
public class GS1Builder {

	private final GS1BuilderParameters parameterManager;
	
	// GS1 Group Separator (GS / FNC1) character terminates variable-length fields in GS1-128 barcodes
	// If the final field is variable length it is not terminated by the GS / FNC1 character
	private static final char GS_FNC = 0x1D; // Group Separator (GS) character

	public GS1Builder(GS1BuilderParameters parameters) {
		this.parameterManager = parameters;
	}
	
	private class ParseResult {
		private GS1BuildParameter buildParam;
		private String value;
		private int nextIndex;
		private boolean finished = false;
	}
	
	private ParseResult extractField(int startIndex, String barcode) {
		ParseResult result = new ParseResult();
		GS1BuildParameter parameter = parameterManager.getParameter(startIndex, barcode); 
	    int identifierLength = parameter.getIdentifierLength();
	    if (parameter.isFixedLength()) {
	        int length = parameter.getPayloadLength();
	        result.value = barcode.substring(startIndex + identifierLength, startIndex + identifierLength + length);
	        result.nextIndex = startIndex + identifierLength + length;
	    } else {
	        int endIndex = barcode.indexOf(GS_FNC, startIndex + identifierLength);
	        if (endIndex == -1) {
	            endIndex = barcode.length();
	        }
	        result.value = barcode.substring(startIndex + identifierLength, endIndex);
	        result.nextIndex = endIndex + 1;
	    }
	    if (result.nextIndex >= barcode.length()) {
	        result.finished = true;
	    }
	    result.buildParam = parameter;
	    return result;
	}
	
	public GSOneBarcode createGSOneFromBarcodeString(String barcode) {
		int index = 0;
		ParseResult result;
		GSOneBarcode gs1 = new GSOneBarcode();
		gs1.setBarcode(barcode);
		try {
			do {
				result = extractField(index, barcode);
				applyWithBeanWrapper(gs1, result);
				index = result.nextIndex; // Move to the next field
			} while (!result.finished);
		} catch (IllegalArgumentException ie) {
			// Illegal barcode but we want to continue
			System.out.println("Illegal GS1 barcode: " + barcode);
			gs1.setBarcode("Illegal - " + barcode);
		}
		return gs1;
	}
	
	public void applyWithBeanWrapper(GSOneBarcode barcode, ParseResult buildParse) {
		BeanWrapper wrapper = new BeanWrapperImpl(barcode);
		try {
			wrapper.setPropertyValue(buildParse.buildParam.getSetterMethod(), buildParse.value);
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
