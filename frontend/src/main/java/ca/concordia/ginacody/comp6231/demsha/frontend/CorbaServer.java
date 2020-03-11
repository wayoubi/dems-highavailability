package ca.concordia.ginacody.comp6231.demsha.frontend;


import ca.concordia.ginacody.comp6231.demsha.frontend.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.frontend.services.EventManagementServiceCORBAImpl;
import dems.EventManagementServiceImplHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorbaServer implements Runnable {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CorbaServer.class);

    /**
     *
     */
    private boolean started;


    /**
     *
     */
    public CorbaServer() {
        this.setStarted(false);
    }

    @Override
    public void run() {
        try {
            this.setStarted(true);

            // create and initialize the ORB //
            String[] args = {"-ORBInitialPort", Integer.toString(Configuration.ORB_PORT), "-ORBInitialHost", Configuration.ORB_HOST};

            // create and initialize the ORB //
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa &amp; activate
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            EventManagementServiceCORBAImpl eventManagementServiceCORBAImpl = new EventManagementServiceCORBAImpl();
            eventManagementServiceCORBAImpl.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(eventManagementServiceCORBAImpl);


            // and cast the reference to a CORBA reference
            dems.EventManagementServiceImpl href = EventManagementServiceImplHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the transient name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt, which is part of the
            // Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            NameComponent path[] = ncRef.to_name(Configuration.SERVER_NAME);
            ncRef.rebind(path, href);

            LOGGER.info(String.format("DEMS %s CORBA Server  ready and waiting ...", Configuration.SERVER_NAME));

            // wait for invocations from clients
            while (this.isStarted()) {
                orb.run();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info(String.format("DEMS %s CORBA Server  Exiting ...", Configuration.SERVER_NAME));
    }

    /**
     * @param started
     */
    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * @return
     */
    public boolean isStarted() {
        return started;
    }
}