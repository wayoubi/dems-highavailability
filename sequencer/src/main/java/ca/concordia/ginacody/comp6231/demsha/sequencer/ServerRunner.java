package ca.concordia.ginacody.comp6231.demsha.sequencer;

import ca.concordia.ginacody.comp6231.demsha.sequencer.config.Configuration;
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
        if(args.length != 4 ) {
            LOGGER.error("Please add required arguments, UDP Listener Port, RM Multicast IP, UDP Multicast Port, Starting Sequence");
            return;
        }

        LOGGER.debug("checking passed UDP Listener port to be valid {}", args[0]);
        try {
            Configuration.UDP_PORT = Integer.parseInt(args[0]);
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed UDP Listener port is invalid {}, default will be used {}", args[0].trim(), Configuration.UDP_PORT);
        }

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

        LOGGER.debug("checking passed Starting Sequence to be valid {}", args[4]);
        try {
            Configuration.MESSAGE_SEQUENCE = Integer.parseInt(args[4]);
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed Starting Sequence is invalid {}, default will be used {}", args[4].trim(), Configuration.UDP_PORT);
        }

        LOGGER.info("Starting UDP Server .....");
        Thread udpServerThread = new Thread(new UDPServer());
        udpServerThread.setName("UDP Server Thread");
        udpServerThread.start();
    }
}
