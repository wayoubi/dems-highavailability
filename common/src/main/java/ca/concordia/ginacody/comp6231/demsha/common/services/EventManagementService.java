package ca.concordia.ginacody.comp6231.demsha.common.services;

import java.rmi.RemoteException;

import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;

/**
 * Hello world!
 */
public interface EventManagementService extends java.rmi.Remote {

    /**
     *
     * @param userName
     * @return
     * @throws EventManagementServiceException
     * @throws RemoteException
     */
    public String login(String userName) throws EventManagementServiceException, RemoteException;

    /**
     *
     * @param eventID
     * @param eventType
     * @param bookingCapacity
     * @return
     * @throws EventManagementServiceException
     * @throws RemoteException
     */
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) throws EventManagementServiceException, RemoteException;

    /**
     *
     * @param eventID
     * @param eventType
     * @return
     * @throws EventManagementServiceException
     * @throws RemoteException
     */
    public String removeEvent(String eventID, EventType eventType) throws EventManagementServiceException, RemoteException;

    /**
     *
     * @param eventType
     * @return
     * @throws EventManagementServiceException
     * @throws RemoteException
     */
    public String listEventAvailability(EventType eventType) throws EventManagementServiceException, RemoteException;

    /**
     *
     * @param customerID
     * @param eventID
     * @param eventType
     * @return
     * @throws EventManagementServiceException
     * @throws RemoteException
     */
    public String bookEvent(String customerID, String eventID, EventType eventType) throws EventManagementServiceException, RemoteException;

    /**
     *
     * @param customerID
     * @return
     * @throws EventManagementServiceException
     * @throws RemoteException
     */
    public String getBookingSchedule(String customerID) throws EventManagementServiceException, RemoteException;

    /**
     *
     * @param customerID
     * @param eventID
     * @param eventType
     * @return
     * @throws EventManagementServiceException
     * @throws RemoteException
     */
    public String cancelEvent(String customerID, String eventID, EventType eventType) throws EventManagementServiceException, RemoteException;

    /**
     *
     * @param customerID
     * @param eventID
     * @param eventType
     * @param oldEventID
     * @param oldEventType
     * @return
     */
    public String swapEvent(String customerID, String eventID, String eventType, String oldEventID, String oldEventType);
}