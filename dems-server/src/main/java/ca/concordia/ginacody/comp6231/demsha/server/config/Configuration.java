package ca.concordia.ginacody.comp6231.demsha.server.config;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

    /**
     *
     */
    public static final String SERVER_LOCATION_PATTERN= "^[A-Z]{3}";

    /**
     *
     */
    public static final Map<String, Integer> UDP_SERVERS_PORTS = new HashMap<>();

    /**
     *
     */
    public static String SERVER_LOCATION;

    /**
     *
     */
    public static String RM_NAME;


    /**
     *
     */
    public static String RMI_REGISTRY_HOST = "localhost";

    /**
     *
     */
    public static int RMI_PORT = 1099;

    /**
     *
     */
    public static int UDP_PORT = 8080;

    /**
     *
     */
    static {
        UDP_SERVERS_PORTS.putIfAbsent("MTL", new Integer(7070));
        UDP_SERVERS_PORTS.putIfAbsent("SHE", new Integer(7071));
        UDP_SERVERS_PORTS.putIfAbsent("QUE", new Integer(7072));
    }
}
