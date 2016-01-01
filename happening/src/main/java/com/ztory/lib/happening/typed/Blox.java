package com.ztory.lib.happening.typed;

/**
 * Created by jonruna on 01/01/16.
 */
public class Blox extends TypedHashMap {

    public Blox() {
        super();
    }

    public Blox(int capacity) {
        super(capacity);
    }

    public Blox set(String key, Object val) {
        put(key, val);
        return this;
    }

}
