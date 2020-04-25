package ca.concordia.ginacody.comp6231.demsha.server.facade;


import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.common.services.EventManagementService;
import ca.concordia.ginacody.comp6231.demsha.server.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.server.processors.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class EventManagementBusinessDelegate implements EventManagementService {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventManagementBusinessDelegate.class);

    /**
     *
     */
    private EventManagementBusinessFacade eventManagementBusinessFacade = new EventManagementBusinessFacade();

    @Override
    public String login(String userName)  throws EventManagementServiceException {
        return eventManagementBusinessFacade.login(userName);
    }

    @Override
    public String addEvent(String eventID, EventType eventType, int bookingCapacity) throws EventManagementServiceException {
        return eventManagementBusinessFacade.addEvent(eventID, eventType, bookingCapacity);
    }

    @Override
    public String removeEvent(String eventID, EventType eventType) throws EventManagementServiceException {
        return eventManagementBusinessFacade.removeEvent(eventID, eventType);
    }

    @Override
    public String listEventAvailability(EventType eventType) throws EventManagementServiceException {
        LOGGER.info("Listing Available Events from local server, EventType {}", eventType);
        StringBuilder stringBuilder = new StringBuilder("Success: ");
        try {
            stringBuilder.append(String.format("+------------------------------------+%s",System.lineSeparator()));
            stringBuilder.append(String.format("| Response from Local Server %s%s", Configuration.SERVER_LOCATION, System.lineSeparator()));
            stringBuilder.append(String.format("+------------------------------------+%s", System.lineSeparator()));
            stringBuilder.append(eventManagementBusinessFacade.listEventAvailability(eventType).replace("$$", System.lineSeparator()));
            stringBuilder.append(System.lineSeparator());
        } catch(EventManagementServiceException e) {
            stringBuilder.append(String.format("%s from local server %s%s", e.getMessage(), Configuration.SERVER_LOCATION, System.lineSeparator()));
        }
        LOGGER.info("Listing Available Events from remote locations, EventType {}", eventType);
        Configuration.UDP_SERVERS_PORTS.keySet().stream().forEach(location -> {
            if(!Configuration.SERVER_LOCATION.equals(location)) {
                LOGGER.info("Listing Available Events from {}, EventType {}", location, eventType);
                RequestProcessor requestProcessor = new RequestProcessor(location, String.format("%s:listEventAvailability:%s:", Configuration.SERVER_LOCATION, eventType.getName()));
                requestProcessor.setName(String.format("Request Processor - %s", requestProcessor.hashCode()));
                requestProcessor.start();
                try {
                    LOGGER.info("Waiting for Listing Available Events from {}, EventType {}", location, eventType);
                    requestProcessor.join();
                } catch (InterruptedException intex) {
                    requestProcessor.interrupt();
                    LOGGER.error("{} caused by {}", intex.getMessage(), intex.getCause().getMessage());
                }
                LOGGER.info("Adding response - Listing Available Events from {}, EventType {}", location, eventType);
                stringBuilder.append(String.format("+-------------------------------------+%s",System.lineSeparator()));
                stringBuilder.append(String.format("| Response from Remote Server %s %s", location, System.lineSeparator()));
                stringBuilder.append(String.format("+-------------------------------------+%s", System.lineSeparator()));
                stringBuilder.append(requestProcessor.getReplyMessage().replace("$$", System.lineSeparator()));
                stringBuilder.append(System.lineSeparator());
            }
        });
        return stringBuilder.toString();
    }

    @Override
    public String bookEvent(String customerID, String eventID, EventType eventType) throws EventManagementServiceException {

        LOGGER.info("Booking an Event customerID {} eventID {} EventType {}", customerID, eventID, eventType);
        AtomicInteger atomicInteger = new AtomicInteger(0);

        String eventLocation = eventID.substring(0,3);
        LOGGER.debug("checking event location exist {}", eventLocation);
        Optional<Integer> optional = Optional.ofNullable(Configuration.UDP_SERVERS_PORTS.get(eventLocation));
        if(!optional.isPresent()) {
            throw new EventManagementServiceException(String.format("Error: Unknown event location %s", eventLocation));
        }

        LOGGER.debug("Checking if Customer {} is Local, Event{} is Local from Local Client {}", customerID, eventID, Configuration.SERVER_LOCATION);
        if(Configuration.SERVER_LOCATION.equals(eventLocation) && customerID.startsWith(Configuration.SERVER_LOCATION)) {
            LOGGER.debug("Local Event, location match customerID {} eventID {} Local location {}",  customerID, eventID, Configuration.SERVER_LOCATION);
            return eventManagementBusinessFacade.bookEvent(customerID, eventID, eventType);
        }

        LOGGER.debug("Checking if Customer {} is Remote, Event{} is Remote but on same location from Remote Client {}", customerID, eventID, Configuration.SERVER_LOCATION);
        if(!Configuration.SERVER_LOCATION.equals(eventLocation) && !customerID.startsWith(Configuration.SERVER_LOCATION) && eventID.substring(0,3).equals(customerID.substring(0,3))) {
            LOGGER.info("Communication with remote server {} to make booking customerID {} eventID {} EventType {}",eventLocation, customerID, eventID, eventType);
            RequestProcessor requestProcessor = new RequestProcessor(eventLocation, String.format("%s:bookEvent:%s:%s:%s:", Configuration.SERVER_LOCATION, customerID, eventID, eventType.getName()));
            requestProcessor.setName(String.format("Request Processor - %s", requestProcessor.hashCode()));
            requestProcessor.start();
            try {
                LOGGER.info("Waiting for Event Booking from {} for customerID {} eventID {} EventType {}", eventLocation, customerID, eventID, eventType);
                requestProcessor.join();
            } catch (InterruptedException intex) {
                requestProcessor.interrupt();
                LOGGER.error("{} caused by {}", intex.getMessage(), intex.getCause().getMessage());
            }
            LOGGER.info("Adding response - Book Event, customerID {} eventID {} EventType {}", customerID, eventID, eventType);
            return requestProcessor.getReplyMessage();
        }

        if(Configuration.SERVER_LOCATION.equals(eventLocation) && !customerID.startsWith(Configuration.SERVER_LOCATION)) {
            //check if customer registered more than 3 times in that week
            //get number of local bookings for this remote user.
            atomicInteger.set(eventManagementBusinessFacade.getBookingCountInSameWeek(customerID.substring(0,8),eventID,eventType));
            if(atomicInteger.get()>=3) {
                LOGGER.info("3 Remote events during same week are already booked for customerID {}, eventID {}, EventType {}", customerID.substring(0,8), eventID,  eventType);
                throw new EventManagementServiceException(String.format("Error: 3 Remote events during same week are already booked for customerID %s, eventID %s, EventType %s", customerID.substring(0,8), eventID,  eventType));
            }
        }

        //get number of local bookings for this remote user.
        //check if customer registered more than 3 times in that week
        Configuration.UDP_SERVERS_PORTS.keySet().stream().forEach(location0 -> {
            if(!Configuration.SERVER_LOCATION.equals(location0)) {
                LOGGER.info("Trying to get booking count from remote server {}, CustomerID {}, eventID{}, EventType {}", eventLocation, customerID.substring(0,8), eventID, eventType);
                RequestProcessor requestProcessor = new RequestProcessor(location0, String.format("%s:getBookingCount:%s:%s:%s:", Configuration.SERVER_LOCATION, customerID.substring(0,8), eventID, eventType.getName()));
                requestProcessor.setName(String.format("Request Processor - %s", requestProcessor.hashCode()));
                requestProcessor.start();
                try {
                    LOGGER.info("Waiting for getBookingCount from {}, customerID {}, eventID {}, EventType {}", location0, customerID.substring(0,8), eventID,  eventType);
                    requestProcessor.join();
                } catch (InterruptedException intex) {
                    requestProcessor.interrupt();
                    LOGGER.error("{} caused by {}", intex.getMessage(), intex.getCause().getMessage());
                }
                try {
                    int counter = Integer.parseInt(requestProcessor.getReplyMessage());
                    atomicInteger.set(atomicInteger.get()+counter);
                } catch (NumberFormatException nfex) {
                    LOGGER.info("Booking failed customerID {} eventID {} eventType {}. Invalid response from remote server {}, error {}", customerID.substring(0,8), eventID,  eventType, location0, requestProcessor.getReplyMessage());
                    throw new EventManagementServiceException(String.format("Error: Booking failed customerID %s eventID %s eventType %s, invalid response from remote server %s, error %s", customerID.substring(0,8), eventID,  eventType, location0,  requestProcessor.getReplyMessage()));
                }
            }
        });

        if(atomicInteger.get()>=3) {
            LOGGER.info("3 Remote events during same week are already booked for customerID {}, eventID {}, EventType {}", customerID.substring(0,8), eventID,  eventType);
            throw new EventManagementServiceException(String.format("Error: 3 Remote events during same week are already booked for customerID %s, eventID %s, EventType %s", customerID.substring(0,8), eventID,  eventType));
        }

        if(Configuration.SERVER_LOCATION.equals(eventLocation)) {
            return eventManagementBusinessFacade.bookEvent(customerID, eventID, eventType);
        }

        LOGGER.info("Communication with remote server {} to make booking customerID {} eventID {} EventType {}", eventLocation, customerID, eventID, eventType);
        RequestProcessor requestProcessor = new RequestProcessor(eventLocation, String.format("%s:bookEvent:%s:%s:%s:", Configuration.SERVER_LOCATION, customerID, eventID, eventType.getName()));
        requestProcessor.setName(String.format("Request Processor - %s", requestProcessor.hashCode()));
        requestProcessor.start();
        try {
            LOGGER.info("Waiting for Event Booking from {} for customerID {} eventID {} EventType {}", eventLocation, customerID, eventID, eventType);
            requestProcessor.join();
        } catch (InterruptedException intex) {
            requestProcessor.interrupt();
            LOGGER.error("{} caused by {}", intex.getMessage(), intex.getCause().getMessage());
        }
        LOGGER.info("Adding response - Book Event, customerID {} eventID {} EventType {}", customerID, eventID, eventType);
        return requestProcessor.getReplyMessage();
    }

    @Override
    public String getBookingSchedule(String customerID) throws EventManagementServiceException {

        LOGGER.info("Listing Booking Schedule for customerID {}", customerID);

        StringBuilder stringBuilder = new StringBuilder("Success: ");
        try {
            stringBuilder.append(String.format("+------------------------------------+%s",System.lineSeparator()));
            stringBuilder.append(String.format("| Response from Local Server %s%s", Configuration.SERVER_LOCATION, System.lineSeparator()));
            stringBuilder.append(String.format("+------------------------------------+%s", System.lineSeparator()));
            stringBuilder.append(eventManagementBusinessFacade.getBookingSchedule(customerID).replace("$$", System.lineSeparator()));
            stringBuilder.append(System.lineSeparator());
        } catch(EventManagementServiceException e) {
            stringBuilder.append(String.format("%s, Response from local server %s%s", e.getMessage(), Configuration.SERVER_LOCATION, System.lineSeparator()));
        }

        Configuration.UDP_SERVERS_PORTS.keySet().stream().forEach(location0 -> {
            if(!Configuration.SERVER_LOCATION.equals(location0)) {
                LOGGER.info("Trying to getBookingSchedule from remote server {}, CustomerID {}", customerID);
                RequestProcessor requestProcessor = new RequestProcessor(location0, String.format("%s:getBookingSchedule:%s:", Configuration.SERVER_LOCATION, customerID));
                requestProcessor.setName(String.format("Request Processor - %s", requestProcessor.hashCode()));
                requestProcessor.start();
                try {
                    LOGGER.info("Waiting for getBookingSchedule from {}, customerID {}", location0, customerID);
                    requestProcessor.join();
                } catch (InterruptedException intex) {
                    requestProcessor.interrupt();
                    LOGGER.error("{} caused by {}", intex.getMessage(), intex.getCause().getMessage());
                }
                LOGGER.info("Adding response - getBookingSchedule from {}, customerID {}", customerID, customerID);
                stringBuilder.append(String.format("+-------------------------------------+%s",System.lineSeparator()));
                stringBuilder.append(String.format("| Response from Remote Server %s %s", location0, System.lineSeparator()));
                stringBuilder.append(String.format("+-------------------------------------+%s", System.lineSeparator()));
                stringBuilder.append(requestProcessor.getReplyMessage().replace("$$", System.lineSeparator()));
                stringBuilder.append(System.lineSeparator());
            }
        });
        return stringBuilder.toString();
    }

    @Override
    public String cancelEvent(String customerID, String eventID, EventType eventType) throws EventManagementServiceException {

        LOGGER.info("Canceling an Event customerID {} eventID {} EventType {}", customerID, eventID, eventType);

        String eventLocation = eventID.substring(0,3);
        LOGGER.debug("checking event location exist {}", eventLocation);
        Optional<Integer> optional = Optional.ofNullable(Configuration.UDP_SERVERS_PORTS.get(eventLocation));
        if(!optional.isPresent()) {
            throw new EventManagementServiceException(String.format("Error: Unknown event location %s", eventLocation));
        }

        LOGGER.debug("Checking if Event{} is Local from Local Client {}", eventID, Configuration.SERVER_LOCATION);
        if(Configuration.SERVER_LOCATION.equals(eventLocation)) {
            LOGGER.debug("Local Event eventID {} Local location {}",  customerID, eventID, Configuration.SERVER_LOCATION);
            return eventManagementBusinessFacade.cancelEvent(customerID, eventID, eventType);
        } else {
            LOGGER.info("Communication with remote server {} to cancel booking customerID {} eventID {} EventType {}",eventLocation , customerID, eventID, eventType);
            RequestProcessor requestProcessor = new RequestProcessor(eventLocation, String.format("%s:cancelEvent:%s:%s:%s:", Configuration.SERVER_LOCATION, customerID, eventID, eventType.getName()));
            requestProcessor.setName(String.format("Request Processor - %s", requestProcessor.hashCode()));
            requestProcessor.start();
            try {
                LOGGER.info("Waiting for Event Canceling from {} for customerID {} eventID {} EventType {}", eventLocation, customerID, eventID, eventType);
                requestProcessor.join();
            } catch (InterruptedException intex) {
                requestProcessor.interrupt();
                LOGGER.error("{} caused by {}", intex.getMessage(), intex.getCause().getMessage());
            }
            LOGGER.info("Adding response - Cancel Event, customerID {} eventID {} EventType {}", customerID, eventID, eventType);
            return requestProcessor.getReplyMessage();
        }
    }

    @Override
    public String swapEvent(String customerID, String eventID, EventType eventType, String oldEventID, EventType oldEventType) {
        String trxNumber = customerID  + UUID.randomUUID().toString();
        String result = this.bookEvent(trxNumber, eventID, eventType);
        if(result.startsWith("Success:")) {
            result = this.cancelEvent(customerID, oldEventID, oldEventType);
            if(result.startsWith("Success:")) {
                String temp = null;
                do {
                    temp = this.commitTrx(trxNumber, eventID);
                    if(temp.startsWith("Success:")) {
                        result = String.format("Success: customerID %s swapped Event %s of Type %s with Event %s of Type %s ", customerID, oldEventID, oldEventType, eventID, eventType);
                    }
                } while(temp.startsWith("Communication Error:"));
            } else {
                String temp = null;
                do {
                    temp = this.cancelEvent(trxNumber, eventID, eventType);
                } while(temp.startsWith("Communication Error:"));
                result = result.replace(trxNumber, customerID);
            }
        } else {
            result = result.replace(trxNumber, customerID);
        }
        return result;
    }

    /**
     *
     * @param trxNumber
     * @return
     */
    public String commitTrx(String trxNumber, String eventID){
        LOGGER.info("Commit Transaction {}", trxNumber);

        String eventLocation = eventID.substring(0,3);

        LOGGER.debug("checking Transaction location exist {}", eventLocation);
        Optional<Integer> optional = Optional.ofNullable(Configuration.UDP_SERVERS_PORTS.get(eventLocation));
        if(!optional.isPresent()) {
            throw new EventManagementServiceException(String.format("Error: Unknown Transaction location %s", eventLocation));
        }

        LOGGER.debug("Checking if Transaction {} is Local from Local Client {}", trxNumber, Configuration.SERVER_LOCATION);
        if(Configuration.SERVER_LOCATION.equals(eventLocation)) {
            LOGGER.debug("Local Transaction {} Local location {}",  trxNumber, Configuration.SERVER_LOCATION);
            return eventManagementBusinessFacade.commitTrx(trxNumber);
        } else {
            LOGGER.info("Communication with remote server {} to commit transaction {}", trxNumber);
            RequestProcessor requestProcessor = new RequestProcessor(eventLocation, String.format("%s:commitTrx:%s:", Configuration.SERVER_LOCATION, trxNumber));
            requestProcessor.setName(String.format("Request Processor - %s", requestProcessor.hashCode()));
            requestProcessor.start();
            try {
                LOGGER.info("Waiting for Commit Transaction from {} for Transaction {}", eventLocation, trxNumber);
                requestProcessor.join();
            } catch (InterruptedException intex) {
                requestProcessor.interrupt();
                LOGGER.error("{} caused by {}", intex.getMessage(), intex.getCause().getMessage());
            }
            LOGGER.info("Adding response - Commit Transaction, Transaction {}", trxNumber);
            return requestProcessor.getReplyMessage();
        }
    }
}