package ca.concordia.ginacody.comp6231.demsha.common.util;

import java.util.*;

public class MessageParser {

    /**
     *
     */
    public Map<String, List<String>> query_pairs;

    /**
     *
     * @param message
     */
    public MessageParser(String message) {
        this.query_pairs = new LinkedHashMap<>();
        this.split(message);
     }

    /**
     *
     * @param command
     * @return
     */
    private Map<String, List<String>> split(String command) {
        final String[] pairs = command.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? pair.substring(0, idx) : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }

    /**
     *
     * @param key
     * @return
     */
    public List<String> getPrameterValues(String key) {
        return this.query_pairs.get(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getPrameterValue(String key) {
        Optional<List<String>> optional = Optional.ofNullable(this.query_pairs.get(key));
        String result = null;
        if(optional.isPresent()){
            result = optional.get().get(0);
        }
        return result;
    }
}