package com.ztory.lib.happening.pod;

import java.util.HashMap;

/**
 * Final class used for querying an HappeningPod instance
 * Created by jonruna on 21/08/15.
 */
public final class PodQ<T> {

    public static PodQ<Void> basic() {
        return new PodQ<>();
    }

    public static <T> PodQ<T> withPayload(T thePayload) {
        return new PodQ<T>().setPayload(thePayload);
    }

    private boolean mAsync = true;
    private int mMode = -1;
    private String mType;
    private T mPayload;

    private HashMap<String, Object> map;

    public PodQ() {
        map = new HashMap<>();
    }

    public boolean isAsync() {
        return mAsync;
    }

    public PodQ<T> setAsync(boolean theAsync) {
        this.mAsync = theAsync;
        return this;
    }

    public T getPayload() {
        return mPayload;
    }

    private PodQ<T> setPayload(T thePayload) {
        mPayload = thePayload;
        return this;
    }

    public int getMode() {
        return mMode;
    }

    public PodQ<T> setMode(int theMode) {
        mMode = theMode;
        return this;
    }

    public String getType() {
        return mType;
    }

    public PodQ<T> setType(String theType) {
        mType = theType;
        return this;
    }

    public PodQ<T> put(String key, Object value) {
        if (value == null) {
            map.remove(key);
        }
        else {
            map.put(key, value);
        }
        return this;
    }

    public Object get(String key) {
        return map.get(key);
    }

    public boolean getBoolean(String key) {
        return (Boolean) map.get(key);
    }

    public int getInt(String key) {
        return (Integer) map.get(key);
    }

    public long getLong(String key) {
        return (Long) map.get(key);
    }

    public double getDouble(String key) {
        return (Double) map.get(key);
    }

    public String getString(String key) {
        return (String) map.get(key);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public boolean contains(String... keys) {
        for (String key : keys) {
            if (!map.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

}
