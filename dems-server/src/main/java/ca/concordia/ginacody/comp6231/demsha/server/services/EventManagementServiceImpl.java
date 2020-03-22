package ca.concordia.ginacody.comp6231.demsha.server.services;



import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.services.EventManagementService;
import ca.concordia.ginacody.comp6231.demsha.server.facade.EventManagementBusinessDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class EventManagementServiceImpl extends UnicastRemoteObject implements EventManagementService {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventManagementServiceImpl.class);

    /**
     *
     * @throws RemoteException
     */
    public EventManagementServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String login(String userName) throws RemoteException {
        EventManagementBusinessDelegate eventManagementBusinessDelegate = new EventManagementBusinessDelegate();
        return eventManagementBusinessDelegate.login(userName);
    }

    @Override
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) throws RemoteException {
        EventManagementBusinessDelegate eventManagementBusinessDelegate = new EventManagementBusinessDelegate();
        return eventManagementBusinessDelegate.addEvent(eventID, eventType, bookingCapacity);
    }

    @Override
    public String removeEvent(String eventID, EventType eventType) throws RemoteException {
        EventManagementBusinessDelegate eventManagementBusinessDelegate = new EventManagementBusinessDelegate();
        return eventManagementBusinessDelegate.removeEvent(eventID, eventType);
    }

    @Override
    public String listEventAvailability(EventType eventType) throws RemoteException {
        EventManagementBusinessDelegate eventManagementBusinessDelegate = new EventManagementBusinessDelegate();
        return eventManagementBusinessDelegate.listEventAvailability(eventType);
    }

    @Override
    public String bookEvent(String customerID, String eventID, EventType eventType) throws RemoteException {
        EventManagementBusinessDelegate eventManagementBusinessDelegate = new EventManagementBusinessDelegate();
        return eventManagementBusinessDelegate.bookEvent(customerID, eventID, eventType);
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        EventManagementBusinessDelegate eventManagementBusinessDelegate = new EventManagementBusinessDelegate();
        return eventManagementBusinessDelegate.getBookingSchedule(customerID);
    }

    @Override
    public String cancelEvent(String customerID, String eventID, EventType eventType) throws RemoteException {
        EventManagementBusinessDelegate eventManagementBusinessDelegate = new EventManagementBusinessDelegate();
        return eventManagementBusinessDelegate.cancelEvent(customerID, eventID, eventType);
    }
}