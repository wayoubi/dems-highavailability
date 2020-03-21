package ca.concordia.ginacody.comp6231.demsha.replicamgr.processor;

import ca.concordia.ginacody.comp6231.demsha.replicamgr.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.replicamgr.vo.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 *
 */
public class ResponseProcessor extends Thread {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseProcessor.class);

    /**
     *
     */
    private String message;

    /**
     *
     * @param message
     */
    public ResponseProcessor(String message) {
        this.message = message;
    }

    @Override
    public void run()  {
        //TODO parse Request
        Request request = null;
        try{
           request = new Request(this.message);
        } catch (IllegalArgumentException e) {
            LOGGER.error(String.format("Request will be ignored, %s", this.message));
            return;
        }

        //TODO Multicast the Message to all Replicas once received from the sequencer
        if("sequencer".equals(request.getPrameterValue(Request.SOURCE))){
            //String temp = this.message.concat(String.format("&%s=%s", Request.SOURCE, Configuration.SERVER_NAME));
            String temp = this.message.replace("source=sequencer", String.format("source=%s", Configuration.SERVER_NAME));
            LOGGER.info("Sending the message to other RM(s) in the cluster");
            MulticastDispatcher multicastDispatcher = new MulticastDispatcher(temp);
            multicastDispatcher.run();
        }

        if(request.isSystem) {
            //TODO if system do something
        }

        if ("sequencer".equals(request.getPrameterValue(Request.SOURCE)) && !request.isSystem) {
            //TODO Reply to FE
            DatagramSocket aSocket = null;
            try {
                aSocket = new DatagramSocket();
                String port = request.getPrameterValue(Request.FRONT_END_PORT)  ;
                InetAddress inetAddress = InetAddress.getByName(Configuration.FRONT_END_HOST);
                LOGGER.info(String.format("Sending the reply to FE on port %s", port));
                this.message = this.message.replace("source=sequencer", String.format("source=%s", Configuration.SERVER_NAME));
                this.message = this.message.concat(String.format("&result=ok"));
                DatagramPacket reply = new DatagramPacket(this.message.getBytes(), this.message.getBytes().length, inetAddress, Integer.parseInt(port));
                aSocket.send(reply);
            } catch (IOException ioex) {
                LOGGER.error("{} caused by {}", ioex.getMessage(), ioex.getCause().getMessage());
            } finally {
                if (aSocket != null)
                    aSocket.close();
            }
        }
    }
}