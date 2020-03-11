package ca.concordia.ginacody.comp6231.demsha.client.cache;



import java.util.HashMap;
import java.util.Map;

public enum UserType {
    CUSTOMER("C"),
    EVENT_MANAGER("M");


    private String name;

    //Lookup table
    private static final Map<String, UserType> lookup = new HashMap<>();

    static {
        for (UserType type : UserType.values()) {
            lookup.put(type.getName(), type);
        }
    }

    UserType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static UserType get(String name) {
        return lookup.get(name);
    }
}
