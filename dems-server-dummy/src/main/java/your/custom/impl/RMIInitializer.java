package your.custom.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIInitializer implements Runnable {

    InputStreamReader is;
    BufferedReader br;

    /**
     *
     */
    public RMIInitializer() {
        is = new InputStreamReader(System.in);
        br = new BufferedReader(is);
    }

    /**
     *
     */
    @Override
    public void run() {

        System.out.println("Enter the RMIregistry Host:");
        String rmiHost;
        try {
            rmiHost = (br.readLine()).trim();
        } catch (IOException e) {
            System.out.println("Default Port number will be used 1099");
            rmiHost = "localhost";
        }

        System.out.println("Enter the RMIregistry port number:");
        String portNum;
        int portNumber = 1099;
        try {
            portNum = (br.readLine()).trim();
            portNumber = Integer.parseInt(portNum);
        } catch (IOException e) {
            System.out.println("Default Port number will be used 1099");
            portNum = "1099";
        }

        System.out.println("Enter the Replica Manager Name:");
        String replicaManagerName;
        try {
            replicaManagerName = (br.readLine()).trim();
        } catch (IOException e) {
            System.out.println("Default Port number will be used RM1");
            replicaManagerName = "RM1";
        }

        System.out.println("Enter Server Location:");
        String serverLocation;
        try {
            serverLocation = (br.readLine()).trim();
        } catch (IOException e) {
            System.out.println("Default Port number will be used MTL");
            serverLocation = "MTL";
        }

        System.out.println("Enter Server Mode [1] Normal [2] Fault");
        String modeStr;
        int mode = Config.NORMAL_MODE;
        try {
            modeStr = (br.readLine()).trim();
            mode = Integer.parseInt(modeStr);
        } catch (IOException e) {
            System.out.println("Default Mode will be used, Normal Mode");
        }

        try {
            startRegistry(portNumber);
            EventManagementServiceImpl exportedObj = new EventManagementServiceImpl();
            String registryURL = String.format("rmi://%s:%s/%s%s", rmiHost, portNumber, replicaManagerName,
                    serverLocation);
            Naming.rebind(registryURL, exportedObj);
            System.out.println("EventManagementServiceImpl is published on " + registryURL);
        } catch (Exception re) {
            re.printStackTrace();
            System.exit(0);
        }
    }

    /**
     *
     * @throws RemoteException
     */
    private static void startRegistry(int portNumber) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", portNumber);
            registry.list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(portNumber);
        }
    }
}
