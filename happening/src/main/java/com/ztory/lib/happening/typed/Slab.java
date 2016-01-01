package com.ztory.lib.happening.typed;

/**
 * Created by jonruna on 01/01/16.
 */
public class Slab<P> extends TypedHashMap implements TypedPayload<P> {

    protected P mPayload;

    public Slab() {
        super();
    }

    public Slab(int capacity) {
        super(capacity);
    }

    public Slab(P thePayload) {
        super();
        setPayload(thePayload);
    }

    public Slab(int capacity, P thePayload) {
        super(capacity);
        setPayload(thePayload);
    }

    public Slab<P> set(String key, Object val) {
        put(key, val);
        return this;
    }

    public Slab<P> setPayload(P thePayload) {
        mPayload = thePayload;
        return this;
    }

    @Override
    public P getPayload() {
        return mPayload;
    }

}
