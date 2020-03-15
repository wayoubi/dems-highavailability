package ca.concordia.ginacody.comp6231.demsha.frontend.services;

import ca.concordia.ginacody.comp6231.demsha.frontend.processor.RequestProcessor;

import ca.concordia.ginacody.comp6231.demsha.common.util.SocketUtils;
import dems.EventManagementServiceImplPOA;
import org.omg.CORBA.ORB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventManagementServiceCORBAImpl extends EventManagementServiceImplPOA {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventManagementServiceCORBAImpl.class);

    /**
     *
     */
    private ORB orb;

     /**
     * @param orb_val
     */
    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public String login(String userName) {
        String message = String.format("command=login&username=%s",userName);
        String result = this.process(message);
        return result;
    }

    @Override
    public String addEvent(String eventID, String eventType, int bookingCapacity) {
        String message = String.format("command=addEvent&eventID=%s&eventType=%s&bookingCapacity=%s",eventID, eventType, bookingCapacity);
        String result = this.process(message);
        return result;
    }

    @Override
    public String removeEvent(String eventID, String eventType) {
        String message = String.format("command=removeEvent&eventID=%s&eventType=%s",eventID, eventType);
        String result = this.process(message);
        return result;
    }

    @Override
    public String listEventAvailability(String eventType) {
        String message = String.format("command=listEventAvailability&eventType=%s",eventType);
        String result = this.process(message);
        return result;
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) {
        String message = String.format("command=bookEvent&customerID=%s&eventID=%s&eventType=%s",customerID, eventID, eventType);
        String result = this.process(message);
        return result;
    }

    @Override
    public String getBookingSchedule(String customerID) {
        String message = String.format("command=getBookingSchedule&customerID=%s",customerID);
        String result = this.process(message);
        return result;
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) {
        String message = String.format("command=cancelEvent&customerID=%s&eventID=%s&eventType=%s",customerID, eventID, eventType);
        String result = this.process(message);
        return result;
    }

    @Override
    public String swapEvent(String customerID, String eventID, String eventType, String oldEventID, String oldEventType) {
        String message = String.format("command=swapEvent&customerID=%s&eventID=%s&eventType=%s&oldEventID=%s&oldEventType=%s",customerID, eventID, eventType, oldEventID, oldEventType);
        String result = this.process(message);
        return result;
    }

    @Override
    public void shutdown() {
        orb.shutdown(false);
    }

    /**
     *
     * @param message
     * @return
     */
    private String process(String message) {
        LOGGER.info(String.format("Starting UDP Listener for new request %s", message));
        RequestProcessor requestProcessor = new RequestProcessor(message);
        requestProcessor.start();
        try {
            requestProcessor.udpListenerThread.join(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StringBuilder stringBuilder = new StringBuilder();
        requestProcessor.replies.values().stream().forEach(s -> {stringBuilder.append(s);});
        if(stringBuilder.length()==0) {
            return "System communication error! Please try again";
        }
        return stringBuilder.toString();
    }
}