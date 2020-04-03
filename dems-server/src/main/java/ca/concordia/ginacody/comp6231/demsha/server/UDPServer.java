package ca.concordia.ginacody.comp6231.demsha.server;


import ca.concordia.ginacody.comp6231.demsha.server.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.server.processors.ResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 */
public class UDPServer implements Runnable {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPServer.class);

    /**
     *
     */
    private boolean started;

    /**
     *
     */
    public UDPServer() {
        this.setStarted(false);
    }

    /**
     *
     */
    @Override
    public void run() {
        this.setStarted(true);
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(Configuration.UDP_PORT);
            LOGGER.info("UDP Server started on port {}, Listening ......", Configuration.UDP_PORT);
            byte[] buffer = new byte[1024];
            while (this.isStarted()) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                LOGGER.info("New message received by UDP Server, will be processed ResponseProcessor");
                ResponseProcessor responseProcessor = new ResponseProcessor(aSocket, request);
                responseProcessor.setName(String.format("Response Processor - ", responseProcessor.hashCode()));
                responseProcessor.start();
            }
        } catch (SocketException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }

    /**
     *
     * @param started
     */
    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     *
     * @return
     */
    public boolean isStarted() {
        return started;
    }
}
