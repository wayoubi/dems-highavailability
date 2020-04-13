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
        System.out.println(String.format("Calling login %s", userName));
        String result = new String();
        if(Config.MODE == Config.NORMAL_MODE) {
            result = "Success: login";
        } else {
            result = "Error: login";
        }
        return result;
    }

    @Override
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) throws RemoteException {
        System.out.println(String.format("Calling addEvent %s %s %s %s", eventID, eventType, bookingCapacity));
        String result = new String();
        if(Config.MODE == Config.NORMAL_MODE) {
            result = "Success: addEvent";
        } else {
            result = "Error: addEvent";
        }
        return result;
    }

    @Override
    public String removeEvent(String eventID, EventType eventType) throws RemoteException {
        System.out.println(String.format("Calling removeEvent %s %s", eventID, eventType));
        String result = new String();
        if(Config.MODE == Config.NORMAL_MODE) {
            result = "Success: removeEvent";
        } else {
            result = "Error: removeEvent";
        }
        return result;
    }

    @Override
    public String listEventAvailability(EventType eventType) throws RemoteException {
        System.out.println(String.format("Calling listEventAvailability %s", eventType));
        String result = new String();
        if(Config.MODE == Config.NORMAL_MODE) {
            result = "Success: listEventAvailability";
        } else {
            result = "Error: listEventAvailability";
        }
        return result;
    }

    @Override
    public String bookEvent(String customerID, String eventID, EventType eventType)
            throws EventManagementServiceException, RemoteException {
        System.out.println(String.format("Calling bookEvent %s %s %s", customerID, eventID, eventType));
        String result = new String();
        if(Config.MODE == Config.NORMAL_MODE) {
            result = "Success: bookEvent";
        } else {
            result = "Error: bookEvent";
        }
        return result;
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        System.out.println(String.format("Calling getBookingSchedule %s", customerID));
        String result = new String();
        if(Config.MODE == Config.NORMAL_MODE) {
            result = "Success: getBookingSchedule";
        } else {
            result = "Error: getBookingSchedule";
        }
        return result;
    }

    @Override
    public String cancelEvent(String customerID, String eventID, EventType eventType) throws RemoteException {
        System.out.println(String.format("Calling cancelEvent %s %s %s", customerID, eventID, eventType));
        String result = new String();
        if(Config.MODE == Config.NORMAL_MODE) {
            result = "Success: cancelEvent";
        } else {
            result = "Error: cancelEvent";
        }
        return result;
    }

    @Override
    public String swapEvent(String customerID, String eventID, EventType eventType, String oldEventID,
                            EventType oldEventType) throws RemoteException {
        System.out.println(String.format("Calling swapEvent %s %s %s %s %s", customerID, eventID, eventType, oldEventID, oldEventType));
        String result = new String();
        if(Config.MODE == Config.NORMAL_MODE) {
            result = "Success: swapEvent";
        } else {
            result = "Error: swapEvent";
        }
        return result;
    }

}
