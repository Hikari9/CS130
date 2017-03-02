package me.ricotiongson.cs130.generic;

import java.util.HashMap;

/**
 * A generic Map that assigns an integer ID for each object put in the map.
 * @param <K>
 */
public class CounterMap<K> extends HashMap<K, Integer> {

    @Override
    public Integer get(Object o) {
        Integer answer = super.get(o);
        if (answer == null)
            put((K) o, answer = size());
        return answer;
    }
}
