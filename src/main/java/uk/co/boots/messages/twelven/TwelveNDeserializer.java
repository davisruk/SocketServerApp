package uk.co.boots.messages.twelven;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.BasicRecord;
import uk.co.boots.messages.Deserializer;
import uk.co.boots.messages.MessageProcessor;
import uk.co.boots.messages.shared.Header;
import uk.co.boots.messages.shared.HeaderSerializationControl;
import uk.co.boots.messages.shared.OrderDetail;
import uk.co.boots.messages.shared.OrderLine;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.messages.shared.ToteIdentifier;
import uk.co.boots.messages.shared.TransportContainer;

@Service
public class TwelveNDeserializer implements Deserializer {

	@Autowired
	private TwelveNSerializationControl twelveNSerializationControl;
	
	@Autowired
	private TwelveNProcessor twelveNProcessor;

	// NOTE - all offsets within SerializationControlField instances are relative
	// i.e
	// they will always begin at 0. The deserialize method keeps a track of the
	// overall
	// offset and uses the SerializationControlField instance offsets to calculate
	// the current position
	@Override
	public BasicMessage deserialize(byte[] messagePayload) {

		Tote record = new Tote();
		record.setHeader(readHeader(messagePayload));

		// now we may get a Tote, Transport, Order Priority, Departure Time or Service
		// Centre in any order
		// read next byte to determine which record type it is
		BasicRecord br = null;
		Header h = record.getHeader();
		int currentOffset = twelveNSerializationControl.getHeaderSerializationControl().getNextDataOffset(h);
		for (int i = 0; i < 5; i++, currentOffset += br
				.getNextRecordOffset()) {
			switch (messagePayload[currentOffset]) {
				case 'T': {
					record.setToteIdentifier(readToteIdentifier(messagePayload, currentOffset));
					br = record.getToteIdentifier();
					br.setTote(record);
					break;
				}
				case 'C': {
					record.setTransportContainer(readTransportContainer(messagePayload, currentOffset));
					br = record.getTransportContainer();
					br.setTote(record);
					break;
				}
				case 'U': {
					record.setOrderPriority(readOrderPriority(messagePayload, currentOffset));
					br = record.getOrderPriority();
					br.setTote(record);
					break;
				}
				case 'E': {
					record.setServiceCentre(readServiceCentre(messagePayload, currentOffset));
					br = record.getServiceCentre();
					br.setTote(record);
					break;
				}
				case 'e': {
					record.setDepartureTime(readDepartureTime(messagePayload, currentOffset));
					br = record.getDepartureTime();
					br.setTote(record);
					break;
				}
			}
		}
		// now read the order lines
		record.setOrderDetail(readOrderLines(record, messagePayload, currentOffset));
		return record;
	}

	private Header readHeader(byte[] payload) {
		HeaderSerializationControl hsc = twelveNSerializationControl.getHeaderSerializationControl();
		Header h = new Header();
		h.setOrderIdLength(
				Integer.parseInt(new String(payload, hsc.getOrderIdInfo().getOffset(), hsc.getOrderIdInfo().getSize())));
		h.setSheetNumberLength(Integer
				.valueOf(new String(payload, hsc.getSheetNumberInfo().getOffset(), hsc.getSheetNumberInfo().getSize())));

		h.setOrderId(new String(payload, hsc.getMessageDataOffset(), h.getOrderIdLength()));
		h.setSheetNumber(new String(payload, hsc.getSheetNumberDataOffset(h), h.getSheetNumberLength()));
		return h;
	}

	private OrderDetail readOrderLines (Tote t, byte[] messagePayload, int offset) {
		OrderDetail od = new OrderDetail();
		od.setTote(t);
		List<OrderLine> ol = od.getOrderLines();
		OrderLineArrayListSerializationControl sc = twelveNSerializationControl.getOrderLineArrayListSerializationControl();
		od.setNumberOfOrderLines(Integer.parseInt(new String(messagePayload, offset + sc.getNumberOrderLinesInfo().getOffset(),  sc.getNumberOrderLinesInfo().getSize())));
		od.setOrderLineReferenceNumberLength(Integer.parseInt(new String(messagePayload, offset + sc.getOrderLineRefInfo().getOffset(),  sc.getOrderLineRefInfo().getSize())));
		od.setOrderLineTypeLength(Integer.parseInt(new String(messagePayload, offset + sc.getOrderLineTypeInfo().getOffset(),  sc.getOrderLineTypeInfo().getSize())));
		od.setPharmacyIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPharmacyIdInfo().getOffset(),  sc.getPharmacyIdInfo().getSize())));
		od.setPatientIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPatientIdInfo().getOffset(),  sc.getPatientIdInfo().getSize())));
		od.setPrescriptionIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPrescriptionIdInfo().getOffset(),  sc.getPrescriptionIdInfo().getSize())));
		od.setProductIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getProductIdInfo().getOffset(),  sc.getProductIdInfo().getSize())));
		od.setNumPacksLength(Integer.parseInt(new String(messagePayload, offset + sc.getNumPacksInfo().getOffset(),  sc.getNumPacksInfo().getSize())));
		od.setPacksPickedLength(Integer.parseInt(new String(messagePayload, offset + sc.getPacksPickedInfo().getOffset(),  sc.getPacksPickedInfo().getSize())));
		od.setNumPillsLength(Integer.parseInt(new String(messagePayload, offset + sc.getNumPillsInfo().getOffset(),  sc.getNumPillsInfo().getSize())));
		od.setRefOrderIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getRefOrderIdInfo().getOffset(),  sc.getRefOrderIdInfo().getSize())));
		od.setRefSheetNumLength(Integer.parseInt(new String(messagePayload, offset + sc.getRefSheetNumInfo().getOffset(),  sc.getRefSheetNumInfo().getSize())));
		for (int i=0, currentOffset = offset + sc.getRefSheetNumInfo().getNextOffset(); i < od.getNumberOfOrderLines(); i++, currentOffset += sc.getNextLineOffset(od) ) {
			OrderLine line = new OrderLine();
			line.setOrderLineNumber(new String(messagePayload, currentOffset + sc.getOrderLineReferenceNumberOffset(od), od.getOrderLineReferenceNumberLength()));
			line.setOrderLineType(new String(messagePayload, currentOffset + sc.getOrderLineTypeOffset(od), od.getOrderLineTypeLength()));
			line.setPharmacyId(new String(messagePayload, currentOffset + sc.getPharmacyIdOffset(od), od.getPharmacyIdLength()));
			line.setPatientId(new String(messagePayload, currentOffset + sc.getPatientIdOffset(od), od.getPatientIdLength()));
			line.setPrescriptionId(new String(messagePayload, currentOffset + sc.getPrescriptionIdOffset(od), od.getPrescriptionIdLength()));
			line.setProductId(new String(messagePayload, currentOffset + sc.getProductIdOffset(od), od.getProductIdLength()));
			line.setNumberOfPacks(new String(messagePayload, currentOffset + sc.getNumPacksOffset(od), od.getNumPacksLength()));
			line.setNumberOfPacksPicked(new String(messagePayload, currentOffset + sc.getPacksPickedOffset(od), od.getPacksPickedLength()));
			line.setNumberOfPills(new String(messagePayload, currentOffset + sc.getNumPillsOffset(od), od.getNumPillsLength()));
			line.setReferenceOrderId(new String(messagePayload, currentOffset + sc.getRefOrderIdOffset(od), od.getRefOrderIdLength()));
			line.setReferenceSheetNumber(new String(messagePayload, currentOffset + sc.getRefSheetNumOffset(od), od.getRefSheetNumLength()));
			line.setOrderDetail(od);
			ol.add(line);
		}
		return od;
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
		return twelveNProcessor;
	}

	@Override
	public boolean canHandle(String messageType) {
		return "12N".equals(messageType);
	}

}
