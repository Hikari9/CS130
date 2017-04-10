package project2;

import java.util.HashMap;

public class Environment extends HashMap<String, Object> {
    @Override
    public Object get(Object o) {
        return super.getOrDefault(o, 0);
    }
}
