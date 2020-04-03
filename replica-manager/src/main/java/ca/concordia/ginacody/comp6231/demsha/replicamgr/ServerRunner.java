package ca.concordia.ginacody.comp6231.demsha.replicamgr;

import ca.concordia.ginacody.comp6231.demsha.replicamgr.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

/**
 *
 */
public class ServerRunner {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRunner.class);

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        LOGGER.debug("checking passed parameters, count {} ", args.length);
        if(args.length != 6) {
            LOGGER.error("Please add required arguments, RM Name, RM Multicast IP, UDP Multicast Port, Frontend Host, RMI Registry Host, RMI Registry Port");
            return;
        }

        LOGGER.debug("checking passed RM Name to be valid {}", args[0]);
        Configuration.SERVER_NAME = args[0];

        LOGGER.debug("checking passed UDP Multicast IP to be valid {}", args[1]);
        if(args[1].startsWith("224") && IPAddressUtil.isIPv4LiteralAddress(args[1])) {
            Configuration.MULTICAST_IP = args[1];
        } else {
            LOGGER.error("Passed UDP Multicast IP is invalid {}, default will be used {}", args[1].trim(), Configuration.MULTICAST_IP);
        }

        LOGGER.debug("checking passed UDP Multicast Port to be valid {}", args[2]);
        try {
            Configuration.MULTICAST_PORT = Integer.parseInt(args[2]);
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed UDP Multicast Port is invalid {}, default will be used {}", args[2].trim(), Configuration.MULTICAST_PORT);
        }

        LOGGER.debug("checking passed Frontend Host to be valid {}", args[3]);
        Configuration.FRONT_END_HOST = args[3];

        LOGGER.debug("checking passed RMI Registry Host to be in valid {}", args[4]);
        Configuration.RMI_REGISTRY_HOST = args[4];

        LOGGER.debug("checking passed RMI Server port to be in valid {}", args[5]);
        try {
            Configuration.RMI_PORT = Integer.parseInt(args[5].trim());
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed RMI Server port is invalid {}, default will be used {}", args[3], Configuration.RMI_PORT);
        }

        LOGGER.info("Starting UDP Server .....");
        Thread udpServerThread = new Thread(new UDPServer());
        udpServerThread.setName("UDP Server Thread");
        udpServerThread.start();
    }
}
