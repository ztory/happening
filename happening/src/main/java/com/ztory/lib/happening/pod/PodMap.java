package com.ztory.lib.happening.pod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonruna on 30/12/15.
 */
public class PodMap extends HashMap<String, Object> {


//    private boolean mAsync = true;
//
//    private int mHttpVerb = -1;
//
//    private String mHttpUrl = null;
//
//    private Map<String, String> mHttpHeaders = null;
//
//    private Map<String, Object> mParamQuery = null;
//
//    private JSONObject mParamJSON = null;
//
//    private boolean mParseString = false, mParseJSON = false;
//
//    private final Type mParseTypeGSON;
//
//    private final Class<G> mParseClassGSON;


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

    public int getId() {
        Integer returnInt = get(Integer.class, KEY_ID);
        if (returnInt != null) {
            return returnInt;
        }
        else {
            return 0;
        }
    }

    public PodMap setId(int id) {
        return set(KEY_ID, id);
    }

    public PodMap set(String key, Object val) {
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
