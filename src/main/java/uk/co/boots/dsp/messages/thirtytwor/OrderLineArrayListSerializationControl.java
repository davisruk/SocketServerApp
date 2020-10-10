package uk.co.boots.dsp.messages.thirtytwor;

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
