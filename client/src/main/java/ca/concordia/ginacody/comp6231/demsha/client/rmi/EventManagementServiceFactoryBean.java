package ca.concordia.ginacody.comp6231.demsha.client.rmi;


import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import ca.concordia.ginacody.comp6231.demsha.client.cache.Session;
import ca.concordia.ginacody.comp6231.demsha.common.services.EventManagementService;

public class EventManagementServiceFactoryBean extends RmiProxyFactoryBean {

    public EventManagementServiceFactoryBean(Session session) {
        this.setServiceUrl(session.getServiceURL()+session.getLocation());
        this.setServiceInterface(EventManagementService.class);
    }
}
