package com.ztory.lib.happening.result;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple object used for equality checks, has a class-unique hashCode() method and
 * a optimized equals() method.
 * Created by jonruna on 28/12/15.
 */
public final class DeedSecret {

    private static AtomicInteger sSecretIdGenerator = new AtomicInteger(0);

    private final int id;

    public DeedSecret() {
        id = sSecretIdGenerator.incrementAndGet();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return o != null &&
               o instanceof DeedSecret &&
               o.hashCode() == hashCode();
    }

}
