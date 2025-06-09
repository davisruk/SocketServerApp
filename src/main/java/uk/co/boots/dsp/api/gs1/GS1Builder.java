package uk.co.boots.dsp.api.gs1;

import java.util.HashMap;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GS1Builder {

	private static int GTIN_PAYLOAD_LENGTH = 14; // GTIN-14 length
	private static int EXPIRY_DATE_PAYLOAD_LENGTH = 6; // Expiry date length (YYMMDD)
	private static int IDENTIFIER_LENGTH = 2; // Length of the identifier prefix for each field
	// GS1 Group Separator (GS / FNC1) character terminates variable-length fields in GS1-128 barcodes
	// If the final field is variable length it is not terminated by the GS / FNC1 character
	private static final char GS_FNC = 0x1D; // Group Separator (GS) character
	
	private HashMap<FieldIdentifier, Integer> fixedLengthFieldLengths = new HashMap<>();
	{
		fixedLengthFieldLengths.put(FieldIdentifier.GTIN, GTIN_PAYLOAD_LENGTH);
		fixedLengthFieldLengths.put(FieldIdentifier.EXPIRY_DATE, EXPIRY_DATE_PAYLOAD_LENGTH);
	}


	public enum FieldIdentifier {
	    GTIN("01"),
	    BATCH_NUMBER("10"),
	    EXPIRY_DATE("17"),
	    SERIAL_NUMBER("21");

	    private final String code;

	    FieldIdentifier(String code) {
	        this.code = code;
	    }

	    public String getCode() {
	        return code;
	    }

	    public static FieldIdentifier fromCode(String code) {
	        for (FieldIdentifier identifier : values()) {
	            if (identifier.getCode().equals(code)) {
	                return identifier;
	            }
	        }
	        throw new IllegalArgumentException("Unknown field identifier: " + code);
	    }
	}
	
	private class ParseResult {
		private FieldIdentifier field;
		private String value;
		private int nextIndex;
		private boolean finished = false;
	}
	
	private boolean isFixedLengthField(FieldIdentifier field) {
		return fixedLengthFieldLengths.containsKey(field);
	}
	
	private int getFixedLength(FieldIdentifier field) {
		return fixedLengthFieldLengths.get(field);
	}
	
	private ParseResult extractField(int startIndex, String barcode) {
	    ParseResult result = new ParseResult();
	    String fieldCode = barcode.substring(startIndex, startIndex + IDENTIFIER_LENGTH);
	    result.field = FieldIdentifier.fromCode(fieldCode);

	    if (isFixedLengthField(result.field)) {
	        int length = getFixedLength(result.field);
	        result.value = barcode.substring(startIndex + IDENTIFIER_LENGTH, startIndex + IDENTIFIER_LENGTH + length);
	        result.nextIndex = startIndex + IDENTIFIER_LENGTH + length;
	    } else {
	        int endIndex = barcode.indexOf(GS_FNC, startIndex + IDENTIFIER_LENGTH);
	        if (endIndex == -1) {
	            endIndex = barcode.length();
	        }
	        result.value = barcode.substring(startIndex + IDENTIFIER_LENGTH, endIndex);
	        result.nextIndex = endIndex + 1;
	    }
	    if (result.nextIndex >= barcode.length()) {
	        result.finished = true;
	    }
	    return result;
	}
	
	public GSOneBarcode createGSOneFromBarcodeString(String barcode) {
		int index = 0;
		ParseResult result;
		GSOneBarcode gs1 = new GSOneBarcode();
		gs1.setBarcode(barcode);
		do {
			result = extractField(index, barcode);
			switch (result.field) {
				case GTIN:
					gs1.setGtin(result.value);
					break;
				case BATCH_NUMBER:
					gs1.setBatchNumber(result.value);
					break;
				case EXPIRY_DATE:
					gs1.setExpiryDate(result.value);
					break;
				case SERIAL_NUMBER:
					gs1.setSerialNumber(result.value);
					break;
				default:
					throw new IllegalArgumentException("Unknown field in GS1 barcode: " + result.field);
			}
			index = result.nextIndex; // Move to the next field
		} while (!result.finished);
		return gs1;
	}
	
	public GSOneBarcode createGSOneFromComponents(String gtin, String batchNumber, String expiryDate, String serialNumber) {
		GSOneBarcode gs1 = new GSOneBarcode();
		gs1.setGtin(gtin);
		gs1.setBatchNumber(batchNumber);
		gs1.setExpiryDate(expiryDate);
		gs1.setSerialNumber(serialNumber);
		String barcode = FieldIdentifier.GTIN.getCode() + gtin
				+ FieldIdentifier.BATCH_NUMBER.getCode() + batchNumber + GS_FNC
				+ FieldIdentifier.EXPIRY_DATE.getCode() + expiryDate
				+ FieldIdentifier.SERIAL_NUMBER.getCode() + serialNumber;
		gs1.setBarcode(barcode);
		return gs1;
	}
}
