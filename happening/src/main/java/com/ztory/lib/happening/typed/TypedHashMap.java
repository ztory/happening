package com.ztory.lib.happening.typed;

import java.util.HashMap;

/**
 * Created by jonruna on 30/12/15.
 */
public class TypedHashMap extends HashMap<String, Object> implements TypedMap<String, Object> {

    public TypedHashMap() {
        super();
    }

    public TypedHashMap(int capacity) {
        super(capacity);
    }

    @Override
    public <T> T typed(String key) {
        return Typed.get(get(key));
    }

    @Override
    public <T> T typed(String key, T defaultValue) {
        return Typed.get(get(key), defaultValue);
    }

}
