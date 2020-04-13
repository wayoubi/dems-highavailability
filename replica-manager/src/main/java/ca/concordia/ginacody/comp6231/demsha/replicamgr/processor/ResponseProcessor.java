package ca.concordia.ginacody.comp6231.demsha.replicamgr.processor;

import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.common.services.EventManagementService;
import ca.concordia.ginacody.comp6231.demsha.replicamgr.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.replicamgr.dao.HoldBackQueue;
import ca.concordia.ginacody.comp6231.demsha.replicamgr.dao.PlayBook;
import ca.concordia.ginacody.comp6231.demsha.replicamgr.vo.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    private static Object mutex = new Object();

    /**
     *
     */
    public static HashMap<Integer, Request> holdBackQueue;

    /**
     *
     */
    private String message;

    /**
     *
     * @param message
     */
    public ResponseProcessor(String message) {
        this.message = message;
        this.holdBackQueue = new LinkedHashMap<>();
    }

    @Override
    public void run()  {
        //parse Request
        Request request = null;
        try{
           request = new Request(this.message);
        } catch (IllegalArgumentException e) {
            LOGGER.error(String.format("Request will be ignored, %s", this.message));
            return;
        }
        // process system messages
        if(request.isSystem) {
            this.handleSystemRequest(request);
        } else {
            this.handleBusinessRequest(request);
        }
    }

    /**
     *
     * @param request
     */
    private void handleSystemRequest(Request request) {
        if("missing-message".equals(request.getPrameterValue(Request.PROBLEM)) && !Configuration.SERVER_NAME.equals(request.getPrameterValue(Request.SOURCE))) {
            LOGGER.info(String.format("Sending missing messages to RM %s", request.getPrameterValue(Request.SOURCE)));
            int start = Integer.parseInt(request.getPrameterValue(Request.CURRENT_SEQUENCE));
            int end = Integer.parseInt(request.getPrameterValue(Request.RECEIVED_SEQUENCE));
            for(int i = start+1; i<=end; i++) {
                LOGGER.info(String.format("Sending missing messages to RM %s, sending message(%s)", request.getPrameterValue(Request.SOURCE), i));
                PlayBook.getInstance().getRecords().computeIfPresent(i, (integer, request1) -> {
                    String oldSource = request1.getPrameterValue(Request.SOURCE);
                    String temp = request1.received.replace(String.format("%s=%s",Request.SOURCE, oldSource), String.format("%s=%s",Request.SOURCE, Configuration.SERVER_NAME));
                    MulticastDispatcher multicastDispatcher = new MulticastDispatcher(temp);
                    multicastDispatcher.run();
                    return request1;
                });
            }
        }
    }

    /**
     *
     * @param request
     */
    private void handleBusinessRequest(Request request) {
        synchronized (mutex) {
            int messageSequence = Integer.parseInt(request.getPrameterValue(Request.SEQUENCE));
            if(messageSequence == Configuration.MESSAGES_SEQUENCE+1 && !HoldBackQueue.getInstance().getQueue().containsKey(messageSequence)) {
                //deliver
                PlayBook.getInstance().addRequest(messageSequence, request);
                Configuration.MESSAGES_SEQUENCE++;
                //Multicast the Message to all Replicas if received from the sequencer
                if("sequencer".equals(request.getPrameterValue(Request.SOURCE))){
                    //String temp = this.message.concat(String.format("&%s=%s", Request.SOURCE, Configuration.SERVER_NAME));
                    String temp = this.message.replace("source=sequencer", String.format("source=%s", Configuration.SERVER_NAME));
                    LOGGER.info(String.format("Sending the message to other RM(s) in the cluster %s", this.message));
                    MulticastDispatcher multicastDispatcher = new MulticastDispatcher(temp);
                    multicastDispatcher.run();
                }
                this.processBusinessRequests(request);
            } else if(messageSequence > Configuration.MESSAGES_SEQUENCE+1 && !HoldBackQueue.getInstance().getQueue().containsKey(messageSequence)) {
                LOGGER.info(String.format("Sequence mismatch, request will be added to the HoldBackQueue Msg Seq:%s, Current Seq: %s", messageSequence, Configuration.MESSAGES_SEQUENCE));
                //put current message in holdbackqueue
                HoldBackQueue.getInstance().addRequest(Integer.valueOf(messageSequence), request);
                //send missing message
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(String.format("command=system&problem=missing-message&source=%s&currentSequence=%s&receivedSequence=%s", Configuration.SERVER_NAME, Configuration.MESSAGES_SEQUENCE, messageSequence));
                MulticastDispatcher multicastDispatcher = new MulticastDispatcher(stringBuilder.toString());
                multicastDispatcher.run();

            } else if (messageSequence < Configuration.MESSAGES_SEQUENCE+1){
                LOGGER.info(String.format("Message was delivered before %s, it will be ignored", request.received));
            }
        }
    }

    /**
     *
     * @param request
     * @return
     */
    private void processBusinessRequests(Request request) {
        //TODO Technical dept in this method
        String replyMessage = null;

        if("login".equals(request.getPrameterValue(Request.COMMAND))) {
            String userName = request.getPrameterValue("username");
            String location = userName.substring(0, 3);
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
            try {
                EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                replyMessage = eventManagementService.login(userName);
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EventManagementServiceException e) {
                replyMessage = e.getMessage();
            }
        }

        if("addEvent".equals(request.getPrameterValue(Request.COMMAND))) {
            //"command=addEvent&eventID=%s&eventType=%s&bookingCapacity=%s"
            String eventID = request.getPrameterValue(Request.EVENT_ID);
            String eventType = request.getPrameterValue(Request.EVENT_TYPE);
            String capacity = request.getPrameterValue(Request.BOOKING_CAPACITY);
            String location = eventID.substring(0,3);
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
            try {
                EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                replyMessage = eventManagementService.addEvent(eventID, EventType.valueOf(eventType), Integer.parseInt(capacity));
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EventManagementServiceException e) {
                replyMessage = e.getMessage();
            }
        }

        if("removeEvent".equals(request.getPrameterValue(Request.COMMAND))) {
            //"command=removeEvent&eventID=%s&eventType=%s"
            String eventID = request.getPrameterValue(Request.EVENT_ID);
            String eventType = request.getPrameterValue(Request.EVENT_TYPE);
            String location = eventID.substring(0,3);
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
            try {
                EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                replyMessage = eventManagementService.removeEvent(eventID, EventType.valueOf(eventType));
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EventManagementServiceException e) {
                replyMessage = e.getMessage();
            }
        }

        if("listEventAvailability".equals(request.getPrameterValue(Request.COMMAND))) {
            //"command=listEventAvailability&eventType=%s"
            String eventType = request.getPrameterValue(Request.EVENT_TYPE);
            String location ="MTL";
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
            try {
                EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                replyMessage = eventManagementService.listEventAvailability(EventType.valueOf(eventType));
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EventManagementServiceException e) {
                replyMessage = e.getMessage();
            }
        }

        if("bookEvent".equals(request.getPrameterValue(Request.COMMAND))) {
            //"command=bookEvent&customerID=%s&eventID=%s&eventType=%s"
            String customerID = request.getPrameterValue(Request.CUSTOMER_ID);
            String eventID = request.getPrameterValue(Request.EVENT_ID);
            String eventType = request.getPrameterValue(Request.EVENT_TYPE);
            String location = customerID.substring(0, 3);
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
            try {
                EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                replyMessage = eventManagementService.bookEvent(customerID, eventID, EventType.valueOf(eventType));
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EventManagementServiceException e) {
                replyMessage = e.getMessage();
            }
        }

        if("getBookingSchedule".equals(request.getPrameterValue(Request.COMMAND))) {
            //"command=getBookingSchedule&customerID=%s"
            String customerID = request.getPrameterValue(Request.CUSTOMER_ID);
            String location = customerID.substring(0, 3);
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
            try {
                EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                replyMessage = eventManagementService.getBookingSchedule(customerID);
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EventManagementServiceException e) {
                replyMessage = e.getMessage();
            }
        }

        if("cancelEvent".equals(request.getPrameterValue(Request.COMMAND))) {
            //"command=cancelEvent&customerID=%s&eventID=%s&eventType=%s"
            String customerID = request.getPrameterValue(Request.CUSTOMER_ID);
            String eventID = request.getPrameterValue(Request.EVENT_ID);
            String eventType = request.getPrameterValue(Request.EVENT_TYPE);
            String location = customerID.substring(0,3);
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
            try {
                EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                replyMessage = eventManagementService.cancelEvent(customerID, eventID, EventType.valueOf(eventType));
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EventManagementServiceException e) {
                replyMessage = e.getMessage();
            }
        }

        if("swapEvent".equals(request.getPrameterValue(Request.COMMAND))) {
            //"command=swapEvent&customerID=%s&eventID=%s&eventType=%s&oldEventID=%s&oldEventType=%s"
            String customerID = request.getPrameterValue(Request.CUSTOMER_ID);
            String eventID = request.getPrameterValue(Request.EVENT_ID);
            String eventType = request.getPrameterValue(Request.EVENT_TYPE);
            String oldEventID = request.getPrameterValue(Request.OLD_EVENT_ID);
            String oldEventType = request.getPrameterValue(Request.OLD_EVENT_TYPE);
            String location = customerID.substring(0,3);
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
            try {
                EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                replyMessage = eventManagementService.swapEvent(customerID, eventID, EventType.valueOf(eventType), oldEventID, EventType.valueOf(oldEventType));
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EventManagementServiceException e) {
                replyMessage = e.getMessage();
            }
        }

        request.processed = true;
        request.response = replyMessage;

        // send reply back to the frontend
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            String port = request.getPrameterValue(Request.FRONT_END_PORT)  ;
            InetAddress inetAddress = InetAddress.getByName(Configuration.FRONT_END_HOST);
            LOGGER.info(String.format("Sending the reply to FE %s on port %s", Configuration.FRONT_END_HOST, port));
            this.message = this.message.replace("source=sequencer", String.format("source=%s", Configuration.SERVER_NAME));
            this.message = this.message.concat(String.format("&result=ok"));
            this.message = this.message.concat(String.format("&message=%s", replyMessage));
            DatagramPacket reply = new DatagramPacket(this.message.getBytes(), this.message.getBytes().length, inetAddress, Integer.parseInt(port));
            aSocket.send(reply);
        } catch (IOException ioex) {
            LOGGER.error("{} caused by {}", ioex.getMessage(), ioex.getCause().getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }

        LOGGER.info(String.format("Check if there are any messages in HoldBackQueue"));
        HoldBackQueue.getInstance().getQueue().computeIfPresent(Integer.valueOf(request.getPrameterValue(Request.SEQUENCE))+1, (integer, request1) -> {
            LOGGER.info(String.format("Message with Sequence %s is in HoldBackQueue, it will be processed now", integer));
            LOGGER.info(String.format("Message with Sequence %s will be removed from HoldBackQueue", integer));
            HoldBackQueue.getInstance().getQueue().remove(integer);
            processBusinessRequests(request1);
            return request1;
        });
    }
}


