package ca.concordia.ginacody.comp6231.demsha.common.services;

import java.rmi.RemoteException;

import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;

/**
 * Hello world!
 */
public interface EventManagementService extends java.rmi.Remote {

    public String login(String userName) throws EventManagementServiceException, RemoteException;

    public String addEvent(String eventID, EventType eventType, int bookingCapacity) throws EventManagementServiceException, RemoteException;

    public String removeEvent(String eventID, EventType eventType) throws EventManagementServiceException, RemoteException;

    public String listEventAvailability(EventType eventType) throws EventManagementServiceException, RemoteException;

    public String bookEvent(String customerID, String eventID, EventType eventType) throws EventManagementServiceException, RemoteException;

    public String getBookingSchedule(String customerID) throws EventManagementServiceException, RemoteException;

    public String cancelEvent(String customerID, String eventID, EventType eventType) throws EventManagementServiceException, RemoteException;
}