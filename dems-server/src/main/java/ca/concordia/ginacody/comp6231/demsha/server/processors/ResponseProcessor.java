package ca.concordia.ginacody.comp6231.demsha.server.processors;


import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.server.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.server.facade.EventManagementBusinessFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 *
 */
public class ResponseProcessor extends Thread {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseProcessor.class);

    /**
     *
     */
    private DatagramPacket request;

    /**
     *
     */
    private DatagramSocket socket;



    /**
     *
     * @param request
     */
    public ResponseProcessor(DatagramSocket aSocket, DatagramPacket request) {
        this.setRequest(request);
        this.setSocket(aSocket);
    }

    @Override
    public void run()  {
        String  commandString = new String(this.getRequest().getData()).substring(0, this.getRequest().getData().length);
        StringTokenizer stringTokenizer = new StringTokenizer(commandString, ":");
        String remoteServer = stringTokenizer.nextToken();
        String command = stringTokenizer.nextToken();

        LOGGER.info("Processing request sent from {} server to {}", remoteServer, command);
        StringBuilder stringBuilder = new StringBuilder();

        if("listEventAvailability".equals(command)) {
            String eventType = stringTokenizer.nextToken();
            Optional<EventType> optional = Optional.ofNullable(EventType.get(eventType));
            if(optional.isPresent()) {
                EventManagementBusinessFacade eventManagementBusinessFacade = new EventManagementBusinessFacade();
                try {
                    stringBuilder.append(eventManagementBusinessFacade.listEventAvailability(optional.get()));
                } catch(EventManagementServiceException e) {
                    stringBuilder.append(String.format("%s, Response from server %s%s", e.getMessage(), Configuration.SERVER_LOCATION,System.lineSeparator()));
                }
            }
        } else if("bookEvent".equals(command)) {
            String customerID = stringTokenizer.nextToken();
            String eventID = stringTokenizer.nextToken();
            String eventType = stringTokenizer.nextToken();
            Optional<EventType> optional = Optional.ofNullable(EventType.get(eventType));
            if(optional.isPresent()) {
               EventManagementBusinessFacade eventManagementBusinessFacade = new EventManagementBusinessFacade();
                try {
                    stringBuilder.append(eventManagementBusinessFacade.bookEvent(customerID, eventID, optional.get()));
                } catch(EventManagementServiceException e) {
                    stringBuilder.append(String.format("%s, Response from remote server %s%s", e.getMessage(), Configuration.SERVER_LOCATION,System.lineSeparator()));
                }
            }
        } else if("getBookingCount".equals(command)) {
            String customerID = stringTokenizer.nextToken();
            String eventID = stringTokenizer.nextToken();
            String eventType = stringTokenizer.nextToken();
            Optional<EventType> optional = Optional.ofNullable(EventType.get(eventType));
            if(optional.isPresent()) {
                EventManagementBusinessFacade eventManagementBusinessFacade = new EventManagementBusinessFacade();
                try {
                    stringBuilder.append(eventManagementBusinessFacade.getBookingCountInSameWeek(customerID, eventID, optional.get()));
                } catch(EventManagementServiceException e) {
                    stringBuilder.append(String.format("%s, Response from remote server %s%s", e.getMessage(), Configuration.SERVER_LOCATION,System.lineSeparator()));
                }
            }
        } else if("getBookingSchedule".equals(command)) {
            String customerID = stringTokenizer.nextToken();
            EventManagementBusinessFacade eventManagementBusinessFacade = new EventManagementBusinessFacade();
            try {
                stringBuilder.append(eventManagementBusinessFacade.getBookingSchedule(customerID));
            } catch(EventManagementServiceException e) {
                stringBuilder.append(String.format("%s, Response from remote server %s%s", e.getMessage(), Configuration.SERVER_LOCATION, System.lineSeparator()));
            }
        } else if("cancelEvent".equals(command)) {
            String customerID = stringTokenizer.nextToken();
            String eventID = stringTokenizer.nextToken();
            String eventType = stringTokenizer.nextToken();
            Optional<EventType> optional = Optional.ofNullable(EventType.get(eventType));
            EventManagementBusinessFacade eventManagementBusinessFacade = new EventManagementBusinessFacade();
            try {
                stringBuilder.append(eventManagementBusinessFacade.cancelEvent(customerID, eventID, optional.get()));
            } catch (EventManagementServiceException e) {
                stringBuilder.append(String.format("%s, Response from remote server %s%s", e.getMessage(), Configuration.SERVER_LOCATION, System.lineSeparator()));
            }
        } else if("commitTrx".equals(command)) {
            String trxNumber = stringTokenizer.nextToken();
            EventManagementBusinessFacade eventManagementBusinessFacade = new EventManagementBusinessFacade();
            try {
                stringBuilder.append(eventManagementBusinessFacade.commitTrx(trxNumber));
            } catch (EventManagementServiceException e) {
                stringBuilder.append(String.format("%s, Response from remote server %s%s", e.getMessage(), Configuration.SERVER_LOCATION, System.lineSeparator()));
            }
        } else {
            LOGGER.warn("Unsupported request {}", this.getRequest().getAddress(), this.getRequest().getPort(), command);
            stringBuilder.append(String.format("Unsupported Operation [%s], Response from remote server %s%s", command, Configuration.SERVER_LOCATION, System.lineSeparator())) ;
        }
        //Send reply back to Client "Request Processor"
        try {
            DatagramPacket reply = new DatagramPacket(stringBuilder.toString().getBytes(), stringBuilder.toString().getBytes().length, request.getAddress(), request.getPort());
            LOGGER.info("sending response back to remote server {} to {} ", remoteServer, command);
            this.getSocket().send(reply);
        } catch (IOException ioex) {
            LOGGER.error("{} caused by {}", ioex.getMessage(), ioex.getCause().getMessage());
        }
    }

    /**
     *
     * @return
     */
    public DatagramPacket getRequest() {
        return request;
    }

    /**
     *
     * @param request
     */
    public void setRequest(DatagramPacket request) {
        this.request = request;
    }

    /**
     *
     * @return
     */
    public DatagramSocket getSocket() {
        return socket;
    }

    /**
     *
     * @param socket
     */
    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }
}