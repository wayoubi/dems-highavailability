package ca.concordia.ginacody.comp6231.demsha.replicamgr.test;

import ca.concordia.ginacody.comp6231.demsha.replicamgr.processor.MulticastDispatcher;

public class TestOrdering {

    public static void main(String args[]) {
        String temp = new String("command=login&username=MTLM1234&feport=8082&source=RM1&sequence=2");
        MulticastDispatcher multicastDispatcher = new MulticastDispatcher(temp);
        multicastDispatcher.run();
    }
}
