package com.ztory.lib.happening;

/**
 * Generic interface used for passing arbitrary parameter and consuming arbitrary return data.
 * #waitingForLambda
 * Created by jonruna on 01/12/15.
 */
public interface Run<R, P> {
    R r(P p);
}
