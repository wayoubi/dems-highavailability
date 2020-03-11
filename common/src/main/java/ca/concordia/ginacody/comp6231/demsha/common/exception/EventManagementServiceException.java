package ca.concordia.ginacody.comp6231.demsha.common.exception;

import java.rmi.RemoteException;

public class EventManagementServiceException extends RuntimeException {
    public EventManagementServiceException(String message) {
        super(message);
    }
}