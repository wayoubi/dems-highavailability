package ca.concordia.ginacody.comp6231.demsha.server;

import ca.concordia.ginacody.comp6231.demsha.server.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.server.services.EventManagementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 */
public class RMIInitializer implements Runnable {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RMIInitializer.class);

    /**
     *
     */
    @Override
    public void run() {
        try{
            startRegistry();
            EventManagementServiceImpl exportedObj = new EventManagementServiceImpl();
            //String registryURL = "rmi://"+ Configuration.RMI_REGISTRY_HOST+":" + Configuration.RMI_PORT + "/EventManagementService" + Configuration.SERVER_LOCATION;
            String registryURL = String.format("rmi://%s:%s/%s%s", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT, Configuration.RM_NAME, Configuration.SERVER_LOCATION );
            Naming.rebind(registryURL, exportedObj);
            LOGGER.info("RMI Object bounded on {}", registryURL);
        }
        catch (Exception re) {
            LOGGER.error("Exception in RMIBasicServer.main: " + re.getMessage());
            System.exit(0);
        }
    }

    /**
     *
     * @throws RemoteException
     */
    private static void startRegistry() throws RemoteException{
        try {
            Registry registry = LocateRegistry.getRegistry(Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT);
            registry.list();
        }
        catch (RemoteException e) {
            LOGGER.error("RMI registry cannot be located at {} {}", Configuration.RMI_REGISTRY_HOST, Configuration.RMI_PORT);
            Registry registry = LocateRegistry.createRegistry(Configuration.RMI_PORT);
            LOGGER.info("RMI registry created at port {}", Configuration.RMI_PORT);
        }
    }
}
