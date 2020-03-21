package ca.concordia.ginacody.comp6231.demsha.replicamgr.vo;

import ca.concordia.ginacody.comp6231.demsha.common.util.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public class Request {

    /**
     * Used parameters
     */
    public static final String COMMAND= "command";
    public static final String CUSTOMER_ID= "customerID";
    public static final String EVENT_ID= "eventID";
    public static final String EVENT_TYPE= "eventType";
    public static final String OLD_EVENT_ID= "oldEventID";
    public static final String OLD_EVENT_TYPE= "oldEventType";
    public static final String PROBLEM= "problem";
    public static final String RM= "rm";
    public static final String BOOKING_CAPACITY="bookingCapacity";
    public static final String FRONT_END_PORT= "feport";
    public static final String SEQUENCE= "sequence";
    public static final String SOURCE= "source";


    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

    /**
     *
     */
    MessageParser messageParser = null;

    /**
     *
     */
    private String received;

    /**
     *
     */
    public boolean isSystem;

    /**
     *
     */
    public int sequence;

    /**
     *
     * @param received
     */
    public Request(String received) throws IllegalArgumentException {
        this.received =  received;
        messageParser = new MessageParser(this.received);

        messageParser.query_pairs.computeIfAbsent(Request.COMMAND, s -> {
            String message = String.format("Message must have one command %s", this.received);
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        });

        messageParser.query_pairs.computeIfPresent(Request.COMMAND, (s, list) -> {
            if(list.size()>1){
                String message = String.format("Message cannot have more than one command %s", this.received);
                LOGGER.error(message);
                throw new IllegalArgumentException(message);
            }
            String command = list.get(0);
            if("system".equals(command)){
                this.isSystem = true;
            }
           return list;
        });

        messageParser.query_pairs.computeIfPresent(Request.FRONT_END_PORT, (s, list) -> {
            if(list.size()>1){
                String message = String.format("Message cannot have more than one front end port %s", this.received);
                LOGGER.error(message);
                throw new IllegalArgumentException(message);
            }
            return list;
        });

        messageParser.query_pairs.computeIfPresent(Request.SEQUENCE, (s, list) -> {
            if(list.size()>1){
                String message = String.format("Message cannot have more than one sequence %s", this.received);
                LOGGER.error(message);
                throw new IllegalArgumentException(message);
            }
            return list;
        });
    }

    /**
     *
     * @param source
     * @return
     */
    public String getPrameterValue(String source) {
        return messageParser.getPrameterValue(source);
    }

    /**
     *  
     * @param source
     * @return
     */
    public List<String> getPrameterValues(String source) {
        return messageParser.getPrameterValues(source);
    }


}