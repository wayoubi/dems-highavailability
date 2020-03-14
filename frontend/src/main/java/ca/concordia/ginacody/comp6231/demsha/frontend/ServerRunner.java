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
        if(args.length != 4 ) {
            LOGGER.error("Please add required arguments, CORBA Naming Service Host, CORBA Naming Service Port, Sequencer Host, Sequencer Port ");
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

        LOGGER.info("Starting CORBA Server .....");
        Thread corbaServerThread = new Thread(new CorbaServer());
        corbaServerThread.setName("CORBA Server Thread");
        corbaServerThread.start();
    }
}
