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
                byte[] buffer = new byte[1024];
                DatagramPacket messagePacket = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(messagePacket);
                String message = new String(messagePacket.getData()).substring(0, messagePacket.getLength());
                Date timestamp = new Date();
                int responseTime = (int) (timestamp.getTime() - this.requestProcessor.timestamp.getTime() );
                LOGGER.info(String.format("New message received from RM %s in %s milliseconds", message, responseTime));
                aSocket.setSoTimeout(responseTime*2);
                this.requestProcessor.messagesBuffer.add(message);
                counter++;
            }
            this.requestProcessor.processReplies();
        } catch (SocketTimeoutException e) {
            this.requestProcessor.processReplies();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("command=system&problem=no-response&source=frontend");
            stringBuilder.append(this.requestProcessor.generateUnresponsiveRMParameterString());
            LOGGER.error(String.format("Not all response(s) are received withing the timeout, RM(s) will be notified %s", stringBuilder.toString()));
            MulticastDispatcher multicastDispatcher = new MulticastDispatcher(stringBuilder.toString());
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