package ca.concordia.ginacody.comp6231.demsha.frontend.processor;

import ca.concordia.ginacody.comp6231.demsha.common.util.SocketUtils;
import ca.concordia.ginacody.comp6231.demsha.frontend.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestProcessor extends Thread {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessor.class);

    /**
     *
     */
    private String message;

    /**
     *
     */
    public Thread udpListenerThread;

    /**
     *
     */
    public Date timestamp;

    /**
     *
     */
    public Map<String, String> replies;

    /**
     *
     * @param message
     */
    public RequestProcessor(String message){
        this.setName("RequestProcessor");
        this.replies = new HashMap<>();
        int port = SocketUtils.findAvailableUdpPort(8000,8100);
        this.message = message.concat(String.format("&feport=%s", port));
        this.udpListenerThread = new Thread(new UDPListener(port, this));
        this.udpListenerThread.setName(String.format("UDP Listener on port %s", port));
        this.udpListenerThread.start();
    }

    /**
     *
     */
    @Override
    public void run() {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte[] m = this.message.getBytes();
            InetAddress aHost = InetAddress.getByName(Configuration.SEQUENCER_HOST);
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, Configuration.SEQUENCER_PORT);
            timestamp = new Date();
            LOGGER.info(String.format("Sending Message: %s", this.message));
            aSocket.send(request);
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