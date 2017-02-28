package me.ricotiongson.cs130.generic;

import java.util.HashMap;

public class CounterMap<K> extends HashMap<K, Integer> {

    @Override
    public Integer get(Object o) {
        Integer answer = super.get(o);
        if (answer == null)
            put((K) o, answer = size());
        return answer;
    }
}
