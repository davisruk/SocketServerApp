package uk.co.boots.dsp.messages.thirtytwor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import uk.co.boots.dsp.messages.SerialisationControlField;
import uk.co.boots.dsp.messages.shared.OrderDetail;
import uk.co.boots.dsp.messages.shared.OrderLine;

@Component("thirtyTwoROrderLineArrayListSerializationControl")
@Data
public class OrderLineArrayListSerializationControl {
	@Autowired
	private OperatorArrayListSerializationControl oalsc;
	
	public static final int BARCODE_DATA_LENGTH = 13;
	public static final int PLASTIC_BAG_ID_DATA_LENGTH = 8;
	public static final int TIMESTAMP_DATA_LENGTH = 17;
	public static final int ROLE_ID_DATA_LENGTH = 20;
	public static final int OPERATOR_ID_DATA_LENGTH = 8;
	public static final int STATUS_DATA_LENGTH = 2;
	
	private final SerialisationControlField identifierInfo = new SerialisationControlField ("%c", 0, 1);
	private final SerialisationControlField numberOrderLinesInfo = new SerialisationControlField ("%03d", identifierInfo.getNextOffset(), 3);
	private final SerialisationControlField orderLineRefInfo = new SerialisationControlField ("%02d", numberOrderLinesInfo.getNextOffset(), 2);
	private final SerialisationControlField orderLineTypeInfo = new SerialisationControlField ("%02d", orderLineRefInfo.getNextOffset(), 2);	
	private final SerialisationControlField pharmacyIdInfo = new SerialisationControlField ("%02d", orderLineTypeInfo.getNextOffset(), 2);
	private final SerialisationControlField patientIdInfo = new SerialisationControlField ("%02d", pharmacyIdInfo.getNextOffset(), 2);
	private final SerialisationControlField prescriptionIdInfo = new SerialisationControlField ("%02d", patientIdInfo.getNextOffset(), 2);
	private final SerialisationControlField plasticBagIdInfo = new SerialisationControlField ("%02d", prescriptionIdInfo.getNextOffset(), 2);
	private final SerialisationControlField productIdInfo = new SerialisationControlField ("%02d", plasticBagIdInfo.getNextOffset(), 2);
	private final SerialisationControlField numPacksInfo = new SerialisationControlField ("%02d", productIdInfo.getNextOffset(), 2);
	private final SerialisationControlField numPillsInfo = new SerialisationControlField ("%02d", numPacksInfo.getNextOffset(), 2);
	private final SerialisationControlField productBarcodeInfo = new SerialisationControlField ("%02d", numPillsInfo.getNextOffset(), 2);
	private final SerialisationControlField operatorIdInfo = new SerialisationControlField ("%02d", productBarcodeInfo.getNextOffset(), 2);
	private final SerialisationControlField roleIdInfo = new SerialisationControlField ("%2d", operatorIdInfo.getNextOffset(), 2);
	private final SerialisationControlField timestampInfo = new SerialisationControlField ("%2d", roleIdInfo.getNextOffset(), 2);
	private SerialisationControlField statusInfo = new SerialisationControlField ("%02d", timestampInfo.getNextOffset(), 2);;

	public static String formatProductBarcode (String barcode) {
		return String.format("%1$" + BARCODE_DATA_LENGTH + "s", barcode);
	}
	
	public static String formatOperatorId(String op) {
		return String.format("%1$" + OPERATOR_ID_DATA_LENGTH + "s", op);
	}

	public static String formatRoleId(String role) {
		return String.format("%1$" + ROLE_ID_DATA_LENGTH + "s", role);	}

	public static String formatTimeStamp(Date time) {
		return new SimpleDateFormat("dd.mm.yy HH.mm.ss").format(time);
	}

	public int getOrderLineReferenceNumberOffset(OrderDetail al) {
		// we don't count numberOfOrderLines
		return 0;
	}
	public int getOrderLineTypeOffset(OrderDetail al) {
		return getOrderLineReferenceNumberOffset(al) + al.getOrderLineReferenceNumberLength();
	}
	
	public int getPharmacyIdOffset(OrderDetail al) {
		return getOrderLineTypeOffset(al) + al.getOrderLineTypeLength(); 
	}
	
	public int getPatientIdOffset(OrderDetail al) {
		return getPharmacyIdOffset(al) + al.getPharmacyIdLength();
	}

	public int getPrescriptionIdOffset(OrderDetail al) {
		return getPatientIdOffset(al) + al.getPatientIdLength();
	}
	
	public int getPlasticBagIdOffset(OrderDetail al) {
		return getPrescriptionIdOffset(al) + al.getPrescriptionIdLength();
	}

	public int getProductIdOffset(OrderDetail al) {
		return getPlasticBagIdOffset(al) + al.getPlasticBagIdLength();
	}
	
	public int getNumPacksOffset(OrderDetail al) {
		return getProductIdOffset(al) + al.getProductIdLength();
	}
	
	public int getNumPillsOffset(OrderDetail al) {
		return getNumPacksOffset(al) + al.getNumPacksLength();
	}
	
	public int getProductBarcodeOffset(OrderDetail al) {
		return getNumPillsOffset(al) + al.getNumPillsLength();
	}
	
	public int getNumberOperatorLinesOffset(OrderDetail al) {
		return getProductBarcodeOffset(al) + al.getProductBarcodeLength();
	}
	
	public int getOperatorsSize(OrderLine ol) {
		OperatorDetail operator = ol.getOperatorDetail();
		OrderDetail order = ol.getOrderDetail();
		int size = oalsc.getNumberOperatorLinesInfo().getSize() + (operator.getNumberOfLines() * (order.getRoleIdLength() + order.getTimestampLength() + order.getOperatorIdLength()));
		return size; 
	}
}
