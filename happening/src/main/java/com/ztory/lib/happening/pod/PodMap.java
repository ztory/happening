package com.ztory.lib.happening.pod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonruna on 30/12/15.
 */
public class PodMap<T> extends HashMap<String, Object> implements PodAsync {

    /**
     * A couple of general keys to be used by subclasses
     */
    public static final String
            KEY_ASYNC = PodMap.class.getName() + ".async",
            KEY_ID = PodMap.class.getName() + ".id",
            KEY_URI = PodMap.class.getName() + ".uri",
            KEY_NAME = PodMap.class.getName() + ".name",
            KEY_TYPE = PodMap.class.getName() + ".type",
            KEY_DATA = PodMap.class.getName() + ".data",
            KEY_PAYLOAD = PodMap.class.getName() + ".payload";

    private boolean mAsync = true;

    private T mPayload;

    public PodMap() {
        super();
    }

    public PodMap(T thePayload) {
        super();

        setMapPayload(thePayload);
    }

    @Override
    public boolean isAsync() {
        return mAsync;
    }

    public PodMap<T> setAsync(boolean theAsync) {
        mAsync = theAsync;
        return this;
    }

    public T getMapPayload() {
        return mPayload;
    }

    public PodMap<T> setMapPayload(T thePayload) {
        mPayload = thePayload;
        return this;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean returnVal = get(Boolean.class, key);
        return (returnVal != null) ? returnVal : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Integer returnVal = get(Integer.class, key);
        return (returnVal != null) ? returnVal : defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        Long returnVal = get(Long.class, key);
        return (returnVal != null) ? returnVal : defaultValue;
    }

    public float getFloat(String key, float defaultValue) {
        Float returnVal = get(Float.class, key);
        return (returnVal != null) ? returnVal : defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        Double returnVal = get(Double.class, key);
        return (returnVal != null) ? returnVal : defaultValue;
    }

    public String getString(String key, String defaultValue) {
        String returnVal = get(String.class, key);
        return (returnVal != null) ? returnVal : defaultValue;
    }

    public Object get(String key, Object defaultValue) {
        Object returnVal = get(key);
        return (returnVal != null) ? returnVal : defaultValue;
    }

    public PodMap<T> set(String key, Object val) {
        put(key, val);
        return this;
    }

    public <T> T get(Class<T> clazz, String key) {
        return get(this, clazz, key);
    }

    public static <T> T get(Map<String, Object> map, Class<T> clazz, String key) {

        if (map == null) {
            return null;
        }

        try {
            return (T) map.get(key);
        } catch (ClassCastException e) {
            return null;
        }
    }

}
