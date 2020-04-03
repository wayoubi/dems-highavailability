package ca.concordia.ginacody.comp6231.demsha.server;

import ca.concordia.ginacody.comp6231.demsha.server.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

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
        if(args.length != 5 ) {
            LOGGER.error("Please add required arguments, Sever Location, Replica Manager Name, RMI Host, RMI Port, UDP Port");
            return;
        }

        LOGGER.debug("checking passed Server Location to be in valid format {} {}", Configuration.SERVER_LOCATION_PATTERN, args[0]);
        Pattern pattern = Pattern.compile(Configuration.SERVER_LOCATION_PATTERN);
        if(!pattern.matcher(args[0]).matches()) {
            LOGGER.error("Passed Server Location is invalid {}", args[0]);
            return;
        } else {
            Configuration.SERVER_LOCATION = args[0].trim();
        }

        LOGGER.debug("checking passed Replica Manager Name to be in valid format {}", args[1]);
        if(!args[1].startsWith("RM")) {
            LOGGER.error("Passed Server Replica Manager is invalid {}", args[1]);
            return;
        } else {
            Configuration.RM_NAME= args[1].trim();
        }

        LOGGER.debug("checking passed RMI Registry Host to be in valid {}", args[2]);
        Configuration.RMI_REGISTRY_HOST = args[2];

        LOGGER.debug("checking passed RMI Server port to be in valid {}", args[3]);
        try {
            Configuration.RMI_PORT = Integer.parseInt(args[3].trim());
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed RMI Server port is invalid {}, default will be used {}", args[3], Configuration.RMI_PORT);
        }

        LOGGER.debug("checking passed UDP Server port to be in valid {}", args[4]);
        try {
            Configuration.UDP_PORT = Integer.parseInt(args[4]);
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed RMI Server port is invalid {}, default will be used {}", args[4].trim(), Configuration.UDP_PORT);
        }

        LOGGER.info("Starting RMI Server .....");
        Thread rmiInitializer = new Thread(new RMIInitializer());
        rmiInitializer.setName("RMIInitializer");
        rmiInitializer.start();

        LOGGER.info("Starting UDP Server .....");
        Thread udpServerThread = new Thread(new UDPServer());
        udpServerThread.setName("UDP Server Thread");
        udpServerThread.start();
    }

}
