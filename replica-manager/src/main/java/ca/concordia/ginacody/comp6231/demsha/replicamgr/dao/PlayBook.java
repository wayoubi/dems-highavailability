package ca.concordia.ginacody.comp6231.demsha.replicamgr.dao;

import ca.concordia.ginacody.comp6231.demsha.replicamgr.vo.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PlayBook {

    /**
     *
     */
    private static PlayBook playBook;

    /**
     *
     */
    Logger LOGGER = LoggerFactory.getLogger(PlayBook.class);

    /**
     *
     */
    private LinkedHashMap<Integer, Request> linkedHashMap;

    /**
     *
     */
    private PlayBook() {
        this.linkedHashMap  = new LinkedHashMap<>();
    }

    /**
     *
     * @return
     */
    public static PlayBook getInstance() {
        if(playBook == null) {
            playBook = new PlayBook();
        }
        return playBook;
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
    public HashMap<Integer, Request> getRecords() {
       return this.linkedHashMap;
    }

}
