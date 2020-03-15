package ca.concordia.ginacody.comp6231.demsha.frontend.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

    /**
     *
     */
    public static final String SERVER_NAME = "frontend";

    /**
     * default 1050
     */
    public static int ORB_PORT = 1050;

    /**
     * default localhost
     */
    public static String ORB_HOST = "localhost";

    /**
     * default localhost
     */
    public static String SEQUENCER_HOST =  "localhost";

    /**
     * default 8081
     */
    public static int SEQUENCER_PORT = 8081;

    /**
     * default 3
     */
    public static int REPLICA_MANAGERS_COUNT = 3;

    /**
     * default 10000 milisecond
     */
    public static int TIMEOUT = 10000;

    /**
     * default 224.0.0.110
     */
    public static String MULTICAST_IP =  "224.0.0.110";

    /**
     * default 4446
     */
    public static int MULTICAST_PORT = 4446;

}
