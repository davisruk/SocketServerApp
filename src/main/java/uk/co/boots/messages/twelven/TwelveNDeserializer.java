package uk.co.boots.messages.twelven;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.BasicRecord;
import uk.co.boots.messages.Deserializer;
import uk.co.boots.messages.MessageProcessor;

public class TwelveNDeserializer implements Deserializer {

	// NOTE - all offsets within SerializationControlField instances are relative
	// i.e
	// they will always begin at 0. The deserialize method keeps a track of the
	// overall
	// offset and uses the SerializationControlField instance offsets to calculate
	// the current position
	@Override
	public BasicMessage deserialize(byte[] messagePayload) {
		TwelveN record = new TwelveN();
		record.setHeader(readHeader(messagePayload));

		// now we may get a Tote, Transport, Order Priority, Departure Time or Service
		// Centre in any order
		// read next byte to determine which record type it is
		BasicRecord br = null;
		int currentOffset = record.getHeader().getMessageDataOffset();
		for (int i = 0; i < 5; i++, currentOffset += br
				.getNextRecordOffset()) {
			switch (messagePayload[currentOffset]) {
				case 'T': {
					record.setToteIdentifier(readToteIdentifier(messagePayload, currentOffset));
					br = record.getToteIdentifier();
					break;
				}
				case 'C': {
					record.setTransportContainer(readTransportContainer(messagePayload, currentOffset));
					br = record.getTransportContainer();
					break;
				}
				case 'U': {
					record.setOrderPriority(readOrderPriority(messagePayload, currentOffset));
					br = record.getOrderPriority();
					break;
				}
				case 'E': {
					record.setServiceCentre(readServiceCentre(messagePayload, currentOffset));
					br = record.getServiceCentre();
					break;
				}
				case 'e': {
					record.setDepartureTime(readDepartureTime(messagePayload, currentOffset));
					br = record.getDepartureTime();
					break;
				}
			}
		}
		// now read the order lines
		record.setOrderLines(readOrderLines(messagePayload, currentOffset));
		return record;
	}

	private Header readHeader(byte[] payload) {
		Header h = new Header();
		h.setOrderIdLength(
				Integer.parseInt(new String(payload, Header.orderIdInfo.getOffset(), Header.orderIdInfo.getSize())));
		h.setSheetNumberLength(Integer
				.valueOf(new String(payload, Header.sheetNumberInfo.getOffset(), Header.sheetNumberInfo.getSize())));

		h.setOrderId(new String(payload, h.getOrderIdDataOffset(), h.getOrderIdLength()));
		h.setSheetNumber(new String(payload, h.getSheetNumberDataOffset(), h.getSheetNumberLength()));
		return h;
	}

	private OrderLineArrayList readOrderLines (byte[] messagePayload, int offset) {
		OrderLineArrayList l = new OrderLineArrayList();
		l.setNumberOfOrderLines(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.numberOrderLinesInfo.getOffset(),  OrderLineArrayList.numberOrderLinesInfo.getSize())));
		l.setOrderLineReferenceNumberLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.orderLineRefInfo.getOffset(),  OrderLineArrayList.orderLineRefInfo.getSize())));
		l.setOrderLineTypeLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.orderLineTypeInfo.getOffset(),  OrderLineArrayList.orderLineTypeInfo.getSize())));
		l.setPharmacyIdLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.pharmacyIdInfo.getOffset(),  OrderLineArrayList.pharmacyIdInfo.getSize())));
		l.setPatientIdLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.patientIdInfo.getOffset(),  OrderLineArrayList.patientIdInfo.getSize())));
		l.setPrescriptionIdLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.prescriptionIdInfo.getOffset(),  OrderLineArrayList.prescriptionIdInfo.getSize())));
		l.setProductIdLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.productIdInfo.getOffset(),  OrderLineArrayList.productIdInfo.getSize())));
		l.setNumPacksLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.numPacksInfo.getOffset(),  OrderLineArrayList.numPacksInfo.getSize())));
		l.setPacksPickedLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.packsPickedInfo.getOffset(),  OrderLineArrayList.packsPickedInfo.getSize())));
		l.setNumPillsLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.numPillsInfo.getOffset(),  OrderLineArrayList.numPillsInfo.getSize())));
		l.setRefOrderIdLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.refOrderIdInfo.getOffset(),  OrderLineArrayList.refOrderIdInfo.getSize())));
		l.setRefSheetNumLength(Integer.parseInt(new String(messagePayload, offset + OrderLineArrayList.refSheetNumInfo.getOffset(),  OrderLineArrayList.refSheetNumInfo.getSize())));
		for (int i=0, currentOffset = offset + OrderLineArrayList.refSheetNumInfo.getNextOffset(); i < l.getNumberOfOrderLines(); i++, currentOffset += l.getNextLineOffset() ) {
			OrderLine line = new OrderLine();
			line.setOrderLineNumber(new String(messagePayload, currentOffset + l.getOrderLineReferenceNumberOffset(), l.getOrderLineReferenceNumberLength()));
			line.setOrderLineType(new String(messagePayload, currentOffset + l.getOrderLineTypeOffset(), l.getOrderLineTypeLength()));
			line.setPharmacyId(new String(messagePayload, currentOffset + l.getPharmacyIdOffset(), l.getPharmacyIdLength()));
			line.setPatientId(new String(messagePayload, currentOffset + l.getPatientIdOffset(), l.getPatientIdLength()));
			line.setPrescriptionId(new String(messagePayload, currentOffset + l.getPrescriptionIdOffset(), l.getPrescriptionIdLength()));
			line.setProductId(new String(messagePayload, currentOffset + l.getProductIdOffset(), l.getProductIdLength()));
			line.setNumberOfPacks(new String(messagePayload, currentOffset + l.getNumPacksOffset(), l.getNumPacksLength()));
			line.setNumberOfPacksPicked(new String(messagePayload, currentOffset + l.getPacksPickedOffset(), l.getPacksPickedLength()));
			line.setNumberOfPills(new String(messagePayload, currentOffset + l.getNumPillsOffset(), l.getNumPillsLength()));
			line.setReferenceOrderId(new String(messagePayload, currentOffset + l.getRefOrderIdOffset(), l.getRefOrderIdLength()));
			line.setReferenceSheetNumber(new String(messagePayload, currentOffset + l.getRefSheetNumOffset(), l.getRefSheetNumLength()));
			l.add(line);
		}
		return l;
	}
	private ToteIdentifier readToteIdentifier(byte[] messagePayload, int offset) {
		ToteIdentifier ti = new ToteIdentifier();
		readBasicPayload(messagePayload, offset, ti);
		return ti;
	}

	private TransportContainer readTransportContainer(byte[] messagePayload, int offset) {
		TransportContainer tc = new TransportContainer();
		readBasicPayload(messagePayload, offset, tc);
		return tc;
	}

	private OrderPriority readOrderPriority(byte[] messagePayload, int offset) {
		OrderPriority op = new OrderPriority();
		readBasicPayload(messagePayload, offset, op);
		return op;
	}

	private ServiceCentre readServiceCentre(byte[] messagePayload, int offset) {
		ServiceCentre sc = new ServiceCentre();
		readBasicPayload(messagePayload, offset, sc);
		return sc;
	}

	private DepartureTime readDepartureTime(byte[] messagePayload, int offset) {
		DepartureTime dt = new DepartureTime();
		readBasicPayload(messagePayload, offset, dt);
		return dt;
	}

	private BasicRecord readBasicPayload(byte[] messagePayload, int offset, BasicRecord br) {
		br.setPayloadLength(Integer.parseInt(new String(messagePayload,
				offset + BasicRecord.fieldLengthInfo.getOffset(), BasicRecord.fieldLengthInfo.getSize())));
		br.setPayload(new String(messagePayload, offset + br.getPayloadDataOffset(), br.getPayloadLength()));
		return br;
	}

	@Override
	public MessageProcessor getProcessor() {
		// TODO Auto-generated method stub
		return new TwelveNProcessor();
	}

}
