package com.ztory.lib.happening.pod;

/**
 * Created by jonruna on 30/12/15.
 */
public interface PodRoperation<Q, D, P> extends PodR<D, P> {

    PodDataPayload<D, P> executeOperation(PodSecret theSecret, Q query) throws PodException;

}
