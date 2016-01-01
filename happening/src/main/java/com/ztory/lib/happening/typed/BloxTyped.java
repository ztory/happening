package com.ztory.lib.happening.typed;

/**
 * Created by jonruna on 01/01/16.
 */
public class BloxTyped<P> extends TypedHashMap implements TypedPayload<P> {

    protected P mPayload;

    public BloxTyped() {
        super();
    }

    public BloxTyped(int capacity) {
        super(capacity);
    }

    public BloxTyped(P thePayload) {
        super();
        setPayload(thePayload);
    }

    public BloxTyped(int capacity, P thePayload) {
        super(capacity);
        setPayload(thePayload);
    }

    public BloxTyped<P> set(String key, Object val) {
        put(key, val);
        return this;
    }

    public BloxTyped<P> setPayload(P thePayload) {
        mPayload = thePayload;
        return this;
    }

    @Override
    public P getPayload() {
        return mPayload;
    }

}
