package ca.concordia.ginacody.comp6231.demsha.replicamgr.config;

import ca.concordia.ginacody.comp6231.demsha.common.util.MessageParser;

public class Configuration {

    /**
     * default RM1
     */
    public static String SERVER_NAME = "RM1";

    /**
     * default 3
     */
    public static int REPLICA_MANAGERS_COUNT = 3;

    /**
     * default 224.0.0.110
     */
    public static String MULTICAST_IP =  "224.0.0.110";

    /**
     * default 4446
     */
    public static int MULTICAST_PORT = 4446;

    /**
     * default;
     */
    public static String FRONT_END_HOST = "localhost";

    /**
     * default localhost
     */
    public static String RMI_REGISTRY_HOST = "localhost";

    /**
     * default 1099
     */
    public static int RMI_PORT = 1099;

    /**
     * starts at 0
     */
    public static int MESSAGES_SEQUENCE = 0;

}
