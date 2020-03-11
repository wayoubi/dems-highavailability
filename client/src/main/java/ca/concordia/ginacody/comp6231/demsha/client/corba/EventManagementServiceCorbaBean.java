package ca.concordia.ginacody.comp6231.demsha.client.corba;

import dems.EventManagementServiceImpl;
import dems.EventManagementServiceImplHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class EventManagementServiceCorbaBean {

    /**
     *
     */
    private static Logger LOGGER = LoggerFactory.getLogger(EventManagementServiceCorbaBean.class);

    @Value( "${dems.corba.naming.port}" )
    private String orbInitialPort;

    @Value( "${dems.corba.naming.host}" )
    private String orbInitialHost;

    /**
     *
     */
    public EventManagementServiceCorbaBean() {
    }

    /**
     *
     * @param city
     * @return
     */
    public dems.EventManagementServiceImpl locateObject(String city) {
        EventManagementServiceImpl eventManagementService = null;
        try {
            String[] args = {"-ORBInitialPort", orbInitialPort, "-ORBInitialHost", orbInitialHost};
            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef = objRef = orb.resolve_initial_references("NameService");
            NamingContextExt namingContextExt = NamingContextExtHelper.narrow(objRef);
            eventManagementService = (EventManagementServiceImpl) EventManagementServiceImplHelper.narrow(namingContextExt.resolve_str(city));
        } catch (NotFound notFound) {
            LOGGER.error(notFound.getMessage());
        } catch (CannotProceed cannotProceed) {
            LOGGER.error(cannotProceed.getMessage());
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
            LOGGER.error(invalidName.getMessage());
        } catch (InvalidName invalidName) {
            LOGGER.error(invalidName.getMessage());
        }
        return eventManagementService;
    }
}