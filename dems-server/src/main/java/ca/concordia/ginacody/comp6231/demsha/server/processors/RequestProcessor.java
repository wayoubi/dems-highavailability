package ca.concordia.ginacody.comp6231.demsha.server.processors;

import ca.concordia.ginacody.comp6231.demsha.server.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.server.services.EventManagementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class RequestProcessor extends Thread {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessor.class);

    /**
     *
     */
    private String requestMessage;

    /**
     *
     */
    private String replyMessage;

    /**
     *
     */
    private String remoteLocation;

    /**
     *
     * @param requestMessage
     */
    public RequestProcessor(String remoteLocation, String requestMessage){
        this.requestMessage = requestMessage;
        this.remoteLocation = remoteLocation;
    }

    /**
     *
     */
    @Override
    public void run() {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            aSocket.setSoTimeout(3000);
            byte[] m = this.requestMessage.getBytes();
            InetAddress aHost = InetAddress.getByName("127.0.0.1");
            int serverPort = Configuration.UDP_SERVERS_PORTS.get(this.remoteLocation);
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            aSocket.send(request);
            byte[] buffer = new byte[1024];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            replyMessage = new String(reply.getData()).substring(0, reply.getLength());
        } catch (SocketException e) {
            this.replyMessage = String.format("Communication Error: Error while communicating with remote server %s, error is $s%s", this.remoteLocation, e.getMessage(), System.lineSeparator());
            LOGGER.error("{}", e.getMessage());
        } catch (IOException e) {
            this.replyMessage = String.format("Communication Error: Error while communicating with remote server %s, error is %s%s", this.remoteLocation, e.getMessage(), System.lineSeparator());
            LOGGER.error("{}", e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    /**
     *
     * @return
     */
    public String getReplyMessage() {
        return replyMessage;
    }
}