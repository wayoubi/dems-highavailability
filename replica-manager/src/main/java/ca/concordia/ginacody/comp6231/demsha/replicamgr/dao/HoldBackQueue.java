package ca.concordia.ginacody.comp6231.demsha.replicamgr.dao;

import ca.concordia.ginacody.comp6231.demsha.replicamgr.vo.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class HoldBackQueue {

    /**
     *
     */
    private static HoldBackQueue holdBackQueue;

    /**
     *
     */
    Logger LOGGER = LoggerFactory.getLogger(HoldBackQueue.class);

    /**
     *
     */
    private LinkedHashMap<Integer, Request> linkedHashMap;

    /**
     *
     */
    private HoldBackQueue() {
        this.linkedHashMap  = new LinkedHashMap<>();
    }

    /**
     *
     * @return
     */
    public static HoldBackQueue getInstance() {
        if(holdBackQueue == null) {
            holdBackQueue = new HoldBackQueue();
        }
        return holdBackQueue;
    }

    /**
     *
     * @param sequence
     * @param request
     */
    public void addRequest(int sequence, Request request) {
        this.linkedHashMap.put(Integer.valueOf(sequence), request);
    }

    /**
     *
     * @return
     */
    public HashMap<Integer, Request> getQueue() {
        return this.linkedHashMap;
    }

}
