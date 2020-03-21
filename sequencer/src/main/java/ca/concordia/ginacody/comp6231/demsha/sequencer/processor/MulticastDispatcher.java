package ca.concordia.ginacody.comp6231.demsha.sequencer.processor;

import ca.concordia.ginacody.comp6231.demsha.sequencer.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class MulticastDispatcher extends Thread {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MulticastDispatcher.class);

    /**
     *
     */
    private String requestMessage;

    /**
     *
     * @param requestMessage
     */
    public MulticastDispatcher(String requestMessage){
        this.requestMessage = requestMessage;
        this.requestMessage = this.requestMessage.replace("source=frontend", "source=sequencer");
    }

    /**
     *
     */
    @Override
    public void run() {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            InetAddress group = InetAddress.getByName(Configuration.MULTICAST_IP);
            byte[] buf = this.requestMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, Configuration.MULTICAST_PORT);
            String message = new String(packet.getData()).substring(0, packet.getData().length);
            aSocket.send(packet);
        } catch (SocketException e) {
            LOGGER.error("{}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("{}", e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }
}