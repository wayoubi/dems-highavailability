package ca.concordia.ginacody.comp6231.demsha.frontend.services;
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
        return "success";
    }

    @Override
    public String addEvent(String eventID, String eventType, int bookingCapacity) {
        return "success";
    }

    @Override
    public String removeEvent(String eventID, String eventType) {
        return "success";
    }

    @Override
    public String listEventAvailability(String eventType) {
        return "success";
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) {
        return "success";
    }

    @Override
    public String getBookingSchedule(String customerID) {
        return "success";
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) {
        return "success";
    }

    @Override
    public String swapEvent(String customerID, String eventID, String eventType, String oldEventID, String oldEventType) {
        return "success";
    }

    @Override
    public void shutdown() {
        orb.shutdown(false);
    }
}