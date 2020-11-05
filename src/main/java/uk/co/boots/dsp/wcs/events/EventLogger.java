/****
 * Utility class for easy logging
 * Be careful when accessing the tote member variables
 * Some may be null as they are set in different parts
 * of the life cycle. Header properties will always be set.
 *****/
package uk.co.boots.dsp.wcs.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventLogger extends DSPEventHandlerAdapter {

	public EventLogger() {
		super("EventLogger");
		// TODO Auto-generated constructor stub
	}

	Logger logger = LoggerFactory.getLogger(EventLogger.class);

	@Override
	public boolean handleEvent(ToteEvent event) {
		boolean handled = true;
		switch (event.getEventType()) {
		case TOTE_ACTIVATED:
			logger.info("[EventLogger::handleEvent] [TOTE_ACTIVATED_EVENT] " + event.getTote().getHeader().getOrderId());
			break;
		case TOTE_DEACTIVATED:
			logger.info("[EventLogger::handleEvent] [TOTE_DEACTIVATED_EVENT] " + event.getTote().getHeader().getOrderId());
			break;
		case TOTE_ORDER_PERSISTED:
			logger.info("[EventLogger::handleEvent] [TOTE_ORDER_PERSISTED_EVENT] " + event.getTote().getHeader().getOrderId());
			break;
		case TOTE_RELEASED_FROM_OSR:
			logger.info("[EventLogger::handleEvent] [TOTE_RELEASED_EVENT] " + event.getTote().getHeader().getOrderId());
			break;
		case TOTE_ORDER_RECEIVED:
			logger.info("[EventLogger::handleEvent] [TOTE_ORDER_RECEIVED_EVENT] " + event.getTote().getHeader().getOrderId());
			break;
		case TOTE_RELEASED_FOR_DELIVERY:
			logger.info("[EventLogger::handleEvent] [TOTE_RELEASED_FOR_DELIVERY_EVENT] " + event.getTote().getHeader().getOrderId());
			break;
		default:
			handled = false;
			break;
		}
		return handled;

	}
}
