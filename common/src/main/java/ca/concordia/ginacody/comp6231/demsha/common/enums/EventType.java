package ca.concordia.ginacody.comp6231.demsha.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum EventType {

    CONFERENCE("CONFERENCE"),
    TRADESHOW("TRADESHOW"),
    SEMINAR("SEMINAR");

    private String name;

    //Lookup table
    private static final Map<String, EventType> lookup = new HashMap<>();

    static {
        for (EventType env : EventType.values()) {
            lookup.put(env.getName(), env);
        }
    }

    EventType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static EventType get(String name) {
        return lookup.get(name);
    }
}
