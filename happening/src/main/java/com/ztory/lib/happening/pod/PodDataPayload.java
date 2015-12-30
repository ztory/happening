package com.ztory.lib.happening.pod;

/**
 * Created by jonruna on 30/12/15.
 */
public class PodDataPayload<D, P> {

    private final D mData;
    private final P mPayload;

    public PodDataPayload(D theData, P thePayload) {
        mData = theData;
        mPayload = thePayload;
    }

    public D getData() {
        return mData;
    }

    public P getPayload() {
        return mPayload;
    }

}
