package ca.concordia.ginacody.comp6231.demsha.server.facade;

import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.common.services.EventManagementService;
import ca.concordia.ginacody.comp6231.demsha.server.dao.BookingDAO;
import ca.concordia.ginacody.comp6231.demsha.server.dao.EventDAO;
import ca.concordia.ginacody.comp6231.demsha.server.vo.EventVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class EventManagementBusinessFacade implements EventManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventManagementBusinessFacade.class);

    @Override
    public String login(String userName) throws EventManagementServiceException {
        LOGGER.info("User {} Logged in", userName);
        //return String.format("User %s logged in successfully, session-id %s", userName, randomUUID().toString());
        return String.format("User %s logged in successfully", userName);
    }

    @Override
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) throws EventManagementServiceException {
        LOGGER.info("Creating Event, EventID {}, EventType {}, Booking Capacity {}", eventID, eventType, bookingCapacity);
        EventVO eventVO = new EventVO(eventID, eventType, bookingCapacity);
        EventDAO eventDAO = new EventDAO();
        return eventDAO.addEvent(eventVO);
    }

    @Override
    public String removeEvent(String eventID, EventType eventType) throws EventManagementServiceException {
        LOGGER.info("Removing Event, EventID {}, EventType {}", eventID, eventType);
        EventVO eventVO = new EventVO(eventID, eventType);
        EventDAO eventDAO = new EventDAO();
        return eventDAO.removeEvent(eventVO);
    }

    @Override
    public String listEventAvailability(EventType eventType) throws EventManagementServiceException {
        LOGGER.info("Listing Available Events, EventType {}", eventType);
        EventDAO eventDAO = new EventDAO();
        return eventDAO.selectAllEvents(eventType);
    }

    @Override
    public String bookEvent(String customerID, String eventID, EventType eventType) throws EventManagementServiceException {
        LOGGER.info("Booking Event, customerID {}, eventID {}, eventType {}", customerID, eventID, eventType);
        EventVO eventVO = new EventVO(eventID, eventType);
        BookingDAO bookingDAO = new BookingDAO();
        return bookingDAO.addBooking(customerID, eventVO);
    }

    @Override
    public String getBookingSchedule(String customerID) throws EventManagementServiceException {
        LOGGER.info("Listing Booking Schedule, customerID {}", customerID);
        BookingDAO bookingDAO = new BookingDAO();
        return bookingDAO.selectAllBookings(customerID);
    }

    @Override
    public String cancelEvent(String customerID, String eventID, EventType eventType) throws EventManagementServiceException {
        LOGGER.info("Cancel Event, customerID {}, eventID {}, eventType{}", customerID, eventID, eventType);
        EventVO eventVO = new EventVO(eventID, eventType);
        BookingDAO bookingDAO = new BookingDAO();
        return bookingDAO.removeBooking(customerID, eventVO);
    }

    @Override
    public String swapEvent(String customerID, String eventID, EventType eventType, String oldEventID, EventType oldEventType) {
        throw new NotImplementedException();
    }

    /**
     *
     * Called from other servers, not exposed to RMI service
     *
     * @param customerID
     * @param eventID
     * @param eventType
     * @return
     */
    public int getBookingCountInSameWeek(String customerID, String eventID, EventType eventType) {
        LOGGER.info("Get Remote Booking Count customerID {}, eventID {}, eventType{}", customerID, eventID, eventType);
        EventVO eventVO = new EventVO(eventID, eventType);
        BookingDAO bookingDAO = new BookingDAO();
        return bookingDAO.countBookingInSameWeek(customerID, eventVO);
    }

    /**
     *
     * @param trxNumber
     * @return
     */
    public String commitTrx(String trxNumber) {
        LOGGER.info("Commit Transaction {}", trxNumber);
        BookingDAO bookingDAO = new BookingDAO();
        return bookingDAO.commit(trxNumber);
    }
}