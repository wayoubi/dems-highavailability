package ca.concordia.ginacody.comp6231.demsha.frontend;

import ca.concordia.ginacody.comp6231.demsha.frontend.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

import java.util.regex.Pattern;

/**
 *
 */
public class    ServerRunner {

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
        if(args.length != 6 ) {
            LOGGER.error("Please add required arguments, CORBA Naming Service Host, CORBA Naming Service Port, Sequencer Host, Sequencer Port, RM Multicast IP, UDP Multicast Port ");
            return;
        }


        LOGGER.debug("checking passed CORBA Naming Service Host to be valid {}", args[0]);
        Configuration.ORB_HOST = args[0];

        LOGGER.debug("checking passed CORBA Naming Service Port to be valid {}", args[1]);
        try {
            Configuration.ORB_PORT = Integer.parseInt(args[1]);
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed ORB port is invalid {}, default will be used {}", args[0].trim(), Configuration.ORB_PORT);
        }

        LOGGER.debug("checking passed Sequencer Host to be valid {}", args[2]);
        Configuration.SEQUENCER_HOST = args[2];

        LOGGER.debug("checking passed Sequencer port to be valid {}", args[3]);
        try {
            Configuration.SEQUENCER_PORT = Integer.parseInt(args[3]);
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed Sequencer port is invalid {}, default will be used {}", args[0].trim(), Configuration.ORB_PORT);
        }

        LOGGER.debug("checking passed UDP Multicast IP to be valid {}", args[4]);
        if(args[4].startsWith("230") && IPAddressUtil.isIPv4LiteralAddress(args[4])) {
            Configuration.MULTICAST_IP = args[4];
        } else {
            LOGGER.error("Passed UDP Multicast IP is invalid {}, default will be used {}", args[4].trim(), Configuration.MULTICAST_IP);
        }

        LOGGER.debug("checking passed UDP Multicast Port to be valid {}", args[5]);
        try {
            Configuration.MULTICAST_PORT = Integer.parseInt(args[5]);
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed UDP Multicast Port is invalid {}, default will be used {}", args[5].trim(), Configuration.MULTICAST_PORT);
        }

        LOGGER.info("Starting CORBA Server .....");
        Thread corbaServerThread = new Thread(new CorbaServer());
        corbaServerThread.setName("CORBA Server Thread");
        corbaServerThread.start();
    }
}
