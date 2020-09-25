package uk.co.boots.messages.twelven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.BasicRecord;
import uk.co.boots.messages.Deserializer;
import uk.co.boots.messages.MessageProcessor;
import uk.co.boots.messages.shared.HeaderSerializationControl;

@Service
public class TwelveNDeserializer implements Deserializer {

	@Autowired
	private TwelveNSerializationControl twelveNSerializationControl;

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
		HeaderSerializationControl hsc = twelveNSerializationControl.getHeaderSerializationControl();
		Header h = new Header();
		h.setOrderIdLength(
				Integer.parseInt(new String(payload, hsc.getOrderIdInfo().getOffset(), hsc.getOrderIdInfo().getSize())));
		h.setSheetNumberLength(Integer
				.valueOf(new String(payload, hsc.sheetNumberInfo.getOffset(), Header.sheetNumberInfo.getSize())));

		h.setOrderId(new String(payload, h.getOrderIdDataOffset(), h.getOrderIdLength()));
		h.setSheetNumber(new String(payload, h.getSheetNumberDataOffset(), h.getSheetNumberLength()));
		return h;
	}

	private OrderLineArrayList readOrderLines (byte[] messagePayload, int offset) {
		OrderLineArrayList l = new OrderLineArrayList();
		OrderLineArrayListSerializationControl sc = twelveNSerializationControl.getOrderLineArrayListSerializationControl();
		l.setNumberOfOrderLines(Integer.parseInt(new String(messagePayload, offset + sc.getNumberOrderLinesInfo().getOffset(),  sc.getNumberOrderLinesInfo().getSize())));
		l.setOrderLineReferenceNumberLength(Integer.parseInt(new String(messagePayload, offset + sc.getOrderLineRefInfo().getOffset(),  sc.getOrderLineRefInfo().getSize())));
		l.setOrderLineTypeLength(Integer.parseInt(new String(messagePayload, offset + sc.getOrderLineTypeInfo().getOffset(),  sc.getOrderLineTypeInfo().getSize())));
		l.setPharmacyIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPharmacyIdInfo().getOffset(),  sc.getPharmacyIdInfo().getSize())));
		l.setPatientIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPatientIdInfo().getOffset(),  sc.getPatientIdInfo().getSize())));
		l.setPrescriptionIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPrescriptionIdInfo().getOffset(),  sc.getPrescriptionIdInfo().getSize())));
		l.setProductIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getProductIdInfo().getOffset(),  sc.getProductIdInfo().getSize())));
		l.setNumPacksLength(Integer.parseInt(new String(messagePayload, offset + sc.getNumPacksInfo().getOffset(),  sc.getNumPacksInfo().getSize())));
		l.setPacksPickedLength(Integer.parseInt(new String(messagePayload, offset + sc.getPacksPickedInfo().getOffset(),  sc.getPacksPickedInfo().getSize())));
		l.setNumPillsLength(Integer.parseInt(new String(messagePayload, offset + sc.getNumPillsInfo().getOffset(),  sc.getNumPillsInfo().getSize())));
		l.setRefOrderIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getRefOrderIdInfo().getOffset(),  sc.getRefOrderIdInfo().getSize())));
		l.setRefSheetNumLength(Integer.parseInt(new String(messagePayload, offset + sc.getRefSheetNumInfo().getOffset(),  sc.getRefSheetNumInfo().getSize())));
		for (int i=0, currentOffset = offset + sc.getRefSheetNumInfo().getNextOffset(); i < l.getNumberOfOrderLines(); i++, currentOffset += l.getNextLineOffset() ) {
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
		return new TwelveNProcessor();
	}

	@Override
	public boolean canHandle(String messageType) {
		return "12N".equals(messageType);
	}

}
