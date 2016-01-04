package com.ztory.lib.happening.typed;

import com.ztory.lib.happening.Run;

/**
 * General purpose data object, capable of serving as a query/parameter or data-return.
 * Can also function as a callback-object by setting the key RUN_INTERFACE to a Run instance.
 * If the Run instance added to RUN_INTERFACE returns RUN_RETURN_SLAB then this Slab
 * instance will be returned instead. If no Run instance is found on the RUN_INTERFACE key then
 * this Slab instance will be returned without any additional computation, this can be used to
 * make static data available from everywhere, but released on specific Happening groupIds.
 * Created by jonruna on 01/01/16.
 */
public class Slab<P> extends TypedHashMap implements TypedPayload<P>, Run {

    public static final Object SLAB_RUN_RETURN = new Object();

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
        return set(PAYLOAD, thePayload);
    }

    @Override
    public P getPayload() {
        return typed(PAYLOAD);
    }

    @Override
    public Object r(Object o) {

        Run callbackRun = typed(RUN_INTERFACE);

        if (callbackRun != null) {
            try {

                Object runReturn = callbackRun.r(o);

                if (SLAB_RUN_RETURN.equals(runReturn)) {
                    return this;
                }
                else {
                    return runReturn;
                }

            } catch (Exception e) {
                return e;
            }
        }

        return this;
    }

}
