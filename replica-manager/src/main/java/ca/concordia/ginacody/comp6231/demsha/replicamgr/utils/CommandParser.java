package ca.concordia.ginacody.comp6231.demsha.replicamgr.utils;

import java.util.*;

public class CommandParser {

    public static Map<String, List<String>> split(String command) {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<>();
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
}