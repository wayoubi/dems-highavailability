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
                this.requestProcessor.replies.put("RM1",message);
                counter++;
            }
        } catch (SocketTimeoutException e) {
            //TODO inform the replica managers about unresponsive replicas, use UDP multicast to do that
            LOGGER.error("Response(s) are not received withing the timeout");
        } catch (SocketException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }
}
