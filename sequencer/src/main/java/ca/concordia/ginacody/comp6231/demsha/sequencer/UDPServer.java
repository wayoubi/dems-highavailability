package ca.concordia.ginacody.comp6231.demsha.sequencer;

import ca.concordia.ginacody.comp6231.demsha.sequencer.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.sequencer.processor.MulticastDispatcher;
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
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(datagramPacket);
                LOGGER.info(String.format("New message received by Sequencer, it be dispatched to RM using UPD Multicast %s:%s", Configuration.MULTICAST_IP, Configuration.MULTICAST_PORT));
                String message = new String(datagramPacket.getData()).substring(0, datagramPacket.getLength());
                message = message.concat(String.format("&sequence=%s", ++Configuration.MESSAGE_SEQUENCE));
                LOGGER.info(String.format("Dispatched Message size (%s) %s ", message.length(), message));
                MulticastDispatcher multicastDispatcher = new MulticastDispatcher(message);
                multicastDispatcher.setName(String.format("Message MulticastDispatcher - ", multicastDispatcher.hashCode()));
                multicastDispatcher.start();
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
