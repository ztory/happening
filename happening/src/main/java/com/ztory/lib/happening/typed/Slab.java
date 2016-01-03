package com.ztory.lib.happening.typed;

import com.ztory.lib.happening.Run;

/**
 * General purpose data object, capable of serving as a query/parameter or data-return.
 * Can also function as a callback-object by setting the key CALLBACK to a Run instance, if
 * this object is used as a callback-object without setting a Run-instance to the CALLBACK key
 * then the callback will return this Slab instance.
 * Created by jonruna on 01/01/16.
 */
public class Slab<P> extends TypedHashMap implements TypedPayload<P>, Run {

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

    @Override
    public Object r(Object o) {

        Run callbackRun = typed(CALLBACK);

        if (callbackRun != null) {
            try {
                return callbackRun.r(o);
            } catch (Exception e) {
                return e;
            }
        }

        return this;
    }

}
