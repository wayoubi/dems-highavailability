package ca.concordia.ginacody.comp6231.demsha.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum EventTimeSlot {

    MORNING("M"),
    AFTERNOON("A"),
    EVENING("E");

    private String name;

    //Lookup table
    private static final Map<String, EventTimeSlot> lookup = new HashMap<>();

    static {
        for (EventTimeSlot env : EventTimeSlot.values()) {
            lookup.put(env.getName(), env);
        }
    }

    EventTimeSlot(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static EventTimeSlot get(String name) {
        return lookup.get(name);
    }
}
