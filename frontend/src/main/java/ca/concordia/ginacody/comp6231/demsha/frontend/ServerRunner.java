package ca.concordia.ginacody.comp6231.demsha.frontend;

import ca.concordia.ginacody.comp6231.demsha.frontend.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if(args.length != 2 ) {
            LOGGER.error("Please add required arguments, ORB Port, ORB Host");
            return;
        }

        LOGGER.debug("checking passed ORB Server port to be valid {}", args[0]);
        try {
            Configuration.ORB_PORT = Integer.parseInt(args[0]);
        } catch(NumberFormatException nfex) {
            LOGGER.error("Passed ORB port is invalid {}, default will be used {}", args[0].trim(), Configuration.ORB_PORT);
        }

        LOGGER.debug("checking passed ORB Host to be valid {}", args[1]);
        Configuration.ORB_HOST = args[1];

        LOGGER.info("Starting CORBA Server .....");
        Thread corbaServerThread = new Thread(new CorbaServer());
        corbaServerThread.setName("CORBA Server Thread");
        corbaServerThread.start();
    }

}
