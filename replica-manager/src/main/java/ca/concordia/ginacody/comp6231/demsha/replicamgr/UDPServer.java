package ca.concordia.ginacody.comp6231.demsha.replicamgr;

import ca.concordia.ginacody.comp6231.demsha.replicamgr.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.replicamgr.processor.ResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

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
        MulticastSocket aSocket = null;
        try {
            aSocket = new MulticastSocket(Configuration.MULTICAST_PORT);
            InetAddress group = InetAddress.getByName(Configuration.MULTICAST_IP);
            aSocket.joinGroup(group);
            while (isStarted()) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                aSocket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                LOGGER.info(String.format("New message received by %s %s", Configuration.SERVER_NAME, received));
                ResponseProcessor responseProcessor = new ResponseProcessor(received);
                responseProcessor.start();
            }
            aSocket.leaveGroup(group);
            aSocket.close();
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
