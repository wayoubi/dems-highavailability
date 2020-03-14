package ca.concordia.ginacody.comp6231.demsha.frontend.processor;

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
     * @param port
     */
    public RequestProcessor(String message, int port){
        this.replies = new HashMap<>();
        this.message = message;
        this.udpListenerThread = new Thread(new UDPListener(port, this));
        this.udpListenerThread.setName(String.format("UDP Server Thread listening on port %s", port));
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
            aSocket.send(request);
            timestamp = new Date();
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