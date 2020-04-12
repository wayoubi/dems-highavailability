package your.custom.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.common.services.EventManagementService;

@SuppressWarnings("serial")
public class EventManagementServiceImpl extends UnicastRemoteObject implements EventManagementService {

    protected EventManagementServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String login(String userName) throws RemoteException {
        System.out.println("Calling login " + userName);
        return "Success: login";
    }

    @Override
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) throws RemoteException {
        return "Success: addEvent";
    }

    @Override
    public String removeEvent(String eventID, EventType eventType) throws RemoteException {
        return "Success: removeEvent";
    }

    @Override
    public String listEventAvailability(EventType eventType) throws RemoteException {
        return "Success: listEventAvailability";
    }

    @Override
    public String bookEvent(String customerID, String eventID, EventType eventType)
            throws EventManagementServiceException, RemoteException {
        return "Success: bookEvent";
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        return "Success: getBookingSchedule";
    }

    @Override
    public String cancelEvent(String customerID, String eventID, EventType eventType) throws RemoteException {
        return "Success: cancelEvent";
    }

    @Override
    public String swapEvent(String customerID, String eventID, EventType eventType, String oldEventID,
                            EventType oldEventType) throws RemoteException {
        return "Success: swapEvent";
    }

}
