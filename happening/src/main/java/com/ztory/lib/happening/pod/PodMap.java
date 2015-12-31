package com.ztory.lib.happening.pod;

import com.ztory.lib.happening.typed.StTyped;
import com.ztory.lib.happening.typed.TypedMap;

import java.util.HashMap;

/**
 * Created by jonruna on 30/12/15.
 */
public class PodMap<P>
        extends HashMap<String, Object>
        implements PodPayload<P>, PodAsync, TypedMap<String, Object>
{

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

    private P mPayload;

    public PodMap() {
        super();
    }

    public PodMap(P thePayload) {
        super();

        setPayload(thePayload);
    }

    @Override
    public boolean isAsync() {
        return mAsync;
    }

    public PodMap<P> setAsync(boolean theAsync) {
        mAsync = theAsync;
        return this;
    }

    @Override
    public P getPayload() {
        return mPayload;
    }

    public PodMap<P> setPayload(P thePayload) {
        mPayload = thePayload;
        return this;
    }

    @Override
    public <T> T getTyped(String key) {
        return StTyped.getTyped(get(key));
    }

    @Override
    public <T> T getTyped(String key, T defaultValue) {
        return StTyped.getTyped(get(key), defaultValue);
    }

    public PodMap<P> set(String key, Object val) {
        put(key, val);
        return this;
    }

}
