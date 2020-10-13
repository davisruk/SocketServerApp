package uk.co.boots.dsp.messages.thirtytwor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.boots.dsp.messages.BasicMessage;
import uk.co.boots.dsp.messages.BasicRecord;
import uk.co.boots.dsp.messages.Deserializer;
import uk.co.boots.dsp.messages.MessageProcessor;
import uk.co.boots.dsp.messages.shared.Header;
import uk.co.boots.dsp.messages.shared.HeaderSerializationControl;
import uk.co.boots.dsp.messages.shared.OrderDetail;
import uk.co.boots.dsp.messages.shared.OrderLine;
import uk.co.boots.dsp.messages.shared.SerializationControlIdentifiers;
import uk.co.boots.dsp.messages.shared.Tote;
import uk.co.boots.dsp.messages.shared.ToteIdentifier;
import uk.co.boots.dsp.messages.shared.TransportContainer;
import uk.co.boots.dsp.wcs.OSRBuffer;

@Service
public class ThirtyTwoRDeserializer implements Deserializer{

	@Autowired
	private ThirtyTwoRSerializationControl thirtyTwoRSerializationControl;
	
	@Autowired
	private OperatorArrayListSerializationControl operatorArrayListSerializationControl;

	@Autowired
	private StatusArrayListSerializationControl statusArrayListSerializationControl;
	
	@Autowired
	private OSRBuffer osrBuffer;
	
	@Override
	public boolean canHandle(String messageType) {
		return "32R".equals(messageType);
	}

	@Override
	public BasicMessage deserialize(byte[] messagePayload) {
		Tote record = new Tote();
		record.setHeader(readHeader(messagePayload));

		// Tote Id, Container Id, Order Priority, Departure Time or Service Centre
		// can appear here in any order they are also non mandatory so may not appear
		// read next byte to determine which record type it is
		BasicRecord br = null;
		Header h = record.getHeader();
		int currentOffset = thirtyTwoRSerializationControl.getHeaderSerializationControl().getNextDataOffset(h);
		int nextOffset = 0;
		while (currentOffset < messagePayload.length && messagePayload[currentOffset] != SerializationControlIdentifiers.ORDER_LIST_32R) {
			switch (messagePayload[currentOffset]) {
				
				case SerializationControlIdentifiers.TOTE_ID: {
					nextOffset = setupToteIdentifier(messagePayload, currentOffset, record);
					break;
				}
				case SerializationControlIdentifiers.CONTAINER_ID: {
					nextOffset = setupTransportContainer(messagePayload, currentOffset, record);
					break;
				}
				case SerializationControlIdentifiers.STATUS: {
					nextOffset = setupToteStatusDetail(messagePayload, currentOffset, record);
					break;
				}
				case SerializationControlIdentifiers.END_TIME: {
					nextOffset = setupEndTime(messagePayload, currentOffset, record);
					break;
				}
				case SerializationControlIdentifiers.START_TIME: {
					nextOffset = setupStartTime(messagePayload, currentOffset, record);
					break;
				}
			}
			currentOffset += nextOffset;
		}
		// check that order lines exist - 32R short doesn't have any
		if (currentOffset < messagePayload.length) {
			// now read the order lines
			record.setOrderDetail(readOrderLines(record, messagePayload, currentOffset));
		}
		return record;
	}

	@Override
	public MessageProcessor getProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	private Header readHeader(byte[] payload) {
		HeaderSerializationControl hsc = thirtyTwoRSerializationControl.getHeaderSerializationControl();
		Header h = new Header();
		h.setOrderIdLength(
				Integer.parseInt(new String(payload, hsc.getOrderIdInfo().getOffset(), hsc.getOrderIdInfo().getSize())));
		h.setSheetNumberLength(Integer
				.valueOf(new String(payload, hsc.getSheetNumberInfo().getOffset(), hsc.getSheetNumberInfo().getSize())));

		h.setOrderId(new String(payload, hsc.getMessageDataOffset(), h.getOrderIdLength()));
		h.setSheetNumber(new String(payload, hsc.getSheetNumberDataOffset(h), h.getSheetNumberLength()));
		return h;
	}

	private int setupToteIdentifier(byte[] messagePayload, int offset, Tote t) {
		ToteIdentifier ti = new ToteIdentifier();
		readBasicPayload(messagePayload, offset, ti);
		ti.setTote(t);
		t.setToteIdentifier(ti);
		return ti.getNextRecordOffset();
	}

	private int setupTransportContainer(byte[] messagePayload, int offset, Tote t) {
		TransportContainer tc = new TransportContainer();
		readBasicPayload(messagePayload, offset, tc);
		tc.setTote(t);
		t.setTransportContainer(tc);
		return tc.getNextRecordOffset();
	}
	
	private int setupEndTime(byte[] messagePayload, int offset, Tote t) {
		EndTime et = new EndTime();
		readBasicPayload(messagePayload, offset, et);
		et.setTote(t);
		t.setEndTime(et);
		return et.getNextRecordOffset();
	}
	
	private int setupStartTime(byte[] messagePayload, int offset, Tote t) {
		StartTime st = new StartTime();
		readBasicPayload(messagePayload, offset, st);
		st.setTote(t);
		t.setStartTime(st);
		return st.getNextRecordOffset();
	}

	private BasicRecord readBasicPayload(byte[] messagePayload, int offset, BasicRecord br) {
		br.setPayloadLength(Integer.parseInt(new String(messagePayload,
				offset + BasicRecord.fieldLengthInfo.getOffset(), BasicRecord.fieldLengthInfo.getSize())));
		br.setPayload(new String(messagePayload, offset + br.getPayloadDataOffset(), br.getPayloadLength()));
		return br;
	}

	
	private int setupToteStatusDetail (byte[] messagePayload, int offset, Tote t) {
		ToteStatusDetail tsd = new ToteStatusDetail();
		t.setStatusDetail(tsd);
		tsd.setIdentifier((char)messagePayload[offset]);
		tsd.setNumberOfLines(Integer.parseInt(new String(messagePayload, offset + statusArrayListSerializationControl.getNumberOfEntries().getOffset(),  statusArrayListSerializationControl.getNumberOfEntries().getSize())));
		tsd.setStatusLength(Integer.parseInt(new String(messagePayload, offset + statusArrayListSerializationControl.getStatusLength().getOffset(), statusArrayListSerializationControl.getStatusLength().getSize())));
		int currentOffset = offset + statusArrayListSerializationControl.getStatusLength().getNextOffset();
		for (int i=0; i < tsd.getNumberOfLines(); i++) {
			Status line = new Status();
			line.setStatus(new String(messagePayload, currentOffset, tsd.getStatusLength()));
			tsd.addStatus(line);
			currentOffset += line.getStatus().length();
		}
		return statusArrayListSerializationControl.getSize(tsd);
	}
	
	private OrderDetail readOrderLines (Tote t, byte[] messagePayload, int offset) {
		OrderDetail od = new OrderDetail();
		od.setTote(t);
		List<OrderLine> ol = od.getOrderLines();
		OrderLineArrayListSerializationControl sc = thirtyTwoRSerializationControl.getThirtyTwoROrderLineArrayListSerializationControl();
		od.setNumberOfOrderLines(Integer.parseInt(new String(messagePayload, offset + sc.getNumberOrderLinesInfo().getOffset(),  sc.getNumberOrderLinesInfo().getSize())));
		od.setOrderLineReferenceNumberLength(Integer.parseInt(new String(messagePayload, offset + sc.getOrderLineRefInfo().getOffset(),  sc.getOrderLineRefInfo().getSize())));
		od.setOrderLineTypeLength(Integer.parseInt(new String(messagePayload, offset + sc.getOrderLineTypeInfo().getOffset(),  sc.getOrderLineTypeInfo().getSize())));
		od.setPharmacyIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPharmacyIdInfo().getOffset(),  sc.getPharmacyIdInfo().getSize())));
		od.setPatientIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPatientIdInfo().getOffset(),  sc.getPatientIdInfo().getSize())));
		od.setPrescriptionIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPrescriptionIdInfo().getOffset(),  sc.getPrescriptionIdInfo().getSize())));
		od.setPlasticBagIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getPlasticBagIdInfo().getOffset(),  sc.getPlasticBagIdInfo().getSize())));
		od.setProductIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getProductIdInfo().getOffset(),  sc.getProductIdInfo().getSize())));
		od.setNumPacksLength(Integer.parseInt(new String(messagePayload, offset + sc.getNumPacksInfo().getOffset(),  sc.getNumPacksInfo().getSize())));
		od.setNumPillsLength(Integer.parseInt(new String(messagePayload, offset + sc.getNumPillsInfo().getOffset(),  sc.getNumPillsInfo().getSize())));
		od.setProductBarcodeLength(Integer.parseInt(new String(messagePayload, offset + sc.getProductBarcodeInfo().getOffset(),  sc.getProductBarcodeInfo().getSize())));
		
		od.setOperatorIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getOperatorIdInfo().getOffset(),  sc.getOperatorIdInfo().getSize())));
		od.setRoleIdLength(Integer.parseInt(new String(messagePayload, offset + sc.getRoleIdInfo().getOffset(),  sc.getRoleIdInfo().getSize())));
		od.setTimestampLength(Integer.parseInt(new String(messagePayload, offset + sc.getTimestampInfo().getOffset(),  sc.getTimestampInfo().getSize())));
		od.setStatusLength(Integer.parseInt(new String(messagePayload, offset + sc.getStatusInfo().getOffset(),  sc.getStatusInfo().getSize())));

		for (int i=0, currentOffset = offset + sc.getStatusInfo().getNextOffset(); i < od.getNumberOfOrderLines(); i++) {
			OrderLine line = new OrderLine();
			line.setOrderDetail(od);
			line.setOrderLineNumber(new String(messagePayload, currentOffset + sc.getOrderLineReferenceNumberOffset(od), od.getOrderLineReferenceNumberLength()));
			line.setOrderLineType(new String(messagePayload, currentOffset + sc.getOrderLineTypeOffset(od), od.getOrderLineTypeLength()));
			line.setPharmacyId(new String(messagePayload, currentOffset + sc.getPharmacyIdOffset(od), od.getPharmacyIdLength()));
			line.setPatientId(new String(messagePayload, currentOffset + sc.getPatientIdOffset(od), od.getPatientIdLength()));
			line.setPrescriptionId(new String(messagePayload, currentOffset + sc.getPrescriptionIdOffset(od), od.getPrescriptionIdLength()));
			line.setProductId(new String(messagePayload, currentOffset + sc.getProductIdOffset(od), od.getProductIdLength()));
			line.setNumberOfPacks(new String(messagePayload, currentOffset + sc.getNumPacksOffset(od), od.getNumPacksLength()));
			line.setNumberOfPills(new String(messagePayload, currentOffset + sc.getNumPillsOffset(od), od.getNumPillsLength()));
			line.setProductBarcode(new String(messagePayload, currentOffset + sc.getProductBarcodeOffset(od), od.getProductBarcodeLength()));
			if (osrBuffer.processingFMD()) {
				int fmdBytes = processFMD(messagePayload, currentOffset, line);
				line.setOperatorDetail(readOperatorDetail(line, messagePayload, currentOffset + sc.getNumberOperatorLinesOffsetWithFMD(od, fmdBytes)));
				line.setStatus(new String(messagePayload, currentOffset + sc.getNumberOperatorLinesOffsetWithFMD(od, fmdBytes) + sc.getOperatorsSize(line), od.getStatusLength()));
				currentOffset += sc.getNumberOperatorLinesOffsetWithFMD(od, fmdBytes) + sc.getOperatorsSize(line) + od.getStatusLength();
			} else {
				line.setOperatorDetail(readOperatorDetail(line, messagePayload, currentOffset + sc.getNumberOperatorLinesOffset(od)));
				line.setStatus(new String(messagePayload, currentOffset + sc.getNumberOperatorLinesOffset(od) + sc.getOperatorsSize(line), od.getStatusLength()));
				currentOffset += sc.getNumberOperatorLinesOffset(od) + sc.getOperatorsSize(line) + od.getStatusLength();				
			}
			ol.add(line);
			
		}
		return od;
	}
	
	// this is not a great implementation but ran out of time and 32R deserialization is not a real requirement, only used for dev and testing 
	private int processFMD (byte[] messagePayload, int offset, OrderLine line) {
		OrderLineArrayListSerializationControl sc = thirtyTwoRSerializationControl.getThirtyTwoROrderLineArrayListSerializationControl();
		OrderDetail od = line.getOrderDetail();
		int bytesProcessed = 0;
		GsOneDetail gsod = new GsOneDetail();
		line.setGsOneDetail(gsod);
		gsod.setOrderLine(line);
		int fmdOffset = offset + sc.getFMDOffset(od);
		int numberOfLines = Integer.parseInt(new String(messagePayload, fmdOffset, OrderLineArrayListSerializationControl.FMD_NUMBER_OF_LINES_LENGTH));
		gsod.setNumberOfLines(numberOfLines);
		bytesProcessed+=OrderLineArrayListSerializationControl.FMD_NUMBER_OF_LINES_LENGTH;
		for (int i = 0; i < numberOfLines; i++) {
			GsOneLine gsol = new GsOneLine();
			gsol.setLengthOfGSone(new String(messagePayload, fmdOffset + bytesProcessed, OrderLineArrayListSerializationControl.FMD_GSONE_LENGTH));
			bytesProcessed += OrderLineArrayListSerializationControl.FMD_GSONE_LENGTH;
			int gsOneLen = Integer.parseInt(gsol.getLengthOfGSone());
			gsol.setGsOne(new String(messagePayload, fmdOffset + bytesProcessed, gsOneLen));
			bytesProcessed+=gsOneLen;
			gsol.setSplitIndicator((char)messagePayload[fmdOffset + bytesProcessed]);
			bytesProcessed ++;
			gsol.setGsOneDetail(gsod);
			gsod.addGsOneLine(gsol);
		}
		return bytesProcessed;
	}
	
	OperatorDetail readOperatorDetail (OrderLine ol, byte[] messagePayload, int offset) {
		OperatorArrayListSerializationControl sc = operatorArrayListSerializationControl;
		OrderDetail orderDetail = ol.getOrderDetail();
		OperatorDetail od = new OperatorDetail();
		od.setOrderLine(ol);
		ol.setOperatorDetail(od);
		od.setNumberOfLines(Integer.parseInt(new String(messagePayload, offset,  sc.getNumberOperatorLinesInfo().getSize())));
		for (int i=0, currentOffset = offset + sc.getNumberOperatorLinesInfo().getNextOffset(); i < od.getNumberOfLines(); i++, currentOffset += sc.getNextLineOffset(orderDetail) ) {
			OperatorLine line = new OperatorLine();
			line.setOperatorId(new String(messagePayload, currentOffset + sc.getOperatorIdOffset(orderDetail), orderDetail.getOperatorIdLength()));
			line.setRoleId(new String(messagePayload, currentOffset + sc.getRoleIdOffset(orderDetail), orderDetail.getRoleIdLength()));
			line.setTimestamp(new String(messagePayload, currentOffset + sc.getTimestampOffset(orderDetail), orderDetail.getTimestampLength()));
			od.addOperatorLine(line);
		}
		return od;
	}

}
