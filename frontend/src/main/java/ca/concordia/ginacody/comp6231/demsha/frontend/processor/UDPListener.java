package ca.concordia.ginacody.comp6231.demsha.frontend.processor;


import ca.concordia.ginacody.comp6231.demsha.frontend.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 *
 */
public class UDPListener implements Runnable {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPListener.class);

    /**
     *
     */
    private int port;

    /**
     *
     */
    private RequestProcessor requestProcessor;


    /**
     *
     */
    public UDPListener(int port, RequestProcessor requestProcessor) {
        this.port = port;
        this.requestProcessor = requestProcessor;
    }

    /**
     *
     */
    @Override
    public void run() {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(port);
            aSocket.setSoTimeout(Configuration.TIMEOUT);
            LOGGER.info("UDP Server started on port {}, Listening ......", port);
            int counter = 0;
            while (counter < Configuration.REPLICA_MANAGERS_COUNT) {
                byte[] buffer = new byte[1000];
                DatagramPacket messagePacket = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(messagePacket);
                LOGGER.info("New message received from RM");
                String message = new String(messagePacket.getData()).substring(0, messagePacket.getData().length);
                Configuration.TIMEOUT = (int) (this.requestProcessor.timestamp.getTime() - new Date().getTime()) * 2;
                aSocket.setSoTimeout(Configuration.TIMEOUT);
                //TODO use RM ID
                this.requestProcessor.replies.put("RM1",message);
                counter++;
            }
        } catch (SocketTimeoutException e) {
            //TODO report failing RM(s)
            String message = "command=system&problem=no-response&rm=RM1,RM2,RM3";
            LOGGER.error(String.format("Response(s) are not received withing the timeout, RM(s) will be notified %s", message));
            MulticastDispatcher multicastDispatcher = new MulticastDispatcher(message);
            multicastDispatcher.setName(String.format("Message MulticastDispatcher - ", multicastDispatcher.hashCode()));
            multicastDispatcher.start();
        } catch (SocketException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
        LOGGER.info(String.format("%s is terminated", Thread.currentThread().getName()));
    }
}