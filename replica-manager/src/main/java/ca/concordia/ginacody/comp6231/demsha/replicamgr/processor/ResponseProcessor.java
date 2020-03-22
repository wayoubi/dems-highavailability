package ca.concordia.ginacody.comp6231.demsha.replicamgr.processor;

import ca.concordia.ginacody.comp6231.demsha.common.services.EventManagementService;
import ca.concordia.ginacody.comp6231.demsha.replicamgr.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.replicamgr.vo.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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


            String replyMessage = null;
            if("login".equals(request.getPrameterValue(Request.COMMAND))) {
                String userName = request.getPrameterValue("username");
                String location = userName.substring(0, 3);
                String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.SERVER_NAME, location);
                try {
                    EventManagementService eventManagementService = (EventManagementService) Naming.lookup(registryURL);
                    replyMessage = eventManagementService.login(userName);
                } catch (NotBoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            DatagramSocket aSocket = null;
            try {
                aSocket = new DatagramSocket();
                String port = request.getPrameterValue(Request.FRONT_END_PORT)  ;
                InetAddress inetAddress = InetAddress.getByName(Configuration.FRONT_END_HOST);
                LOGGER.info(String.format("Sending the reply to FE on port %s", port));
                this.message = this.message.replace("source=sequencer", String.format("source=%s", Configuration.SERVER_NAME));
                this.message = this.message.concat(String.format("&result=ok"));
                this.message = this.message.concat(String.format("&message=%s", replyMessage));

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