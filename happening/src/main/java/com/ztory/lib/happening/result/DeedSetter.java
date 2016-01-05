package com.ztory.lib.happening.result;

/**
 * Extention of the Deed interfaced, complete with success and failed methods!
 * Created by jonruna on 30/12/15.
 */
public interface DeedSetter<D, P> extends Deed<D, P> {

    void setSuccess(DeedSecret theSecret, D theData) throws DeedException;

    void setSuccess(DeedSecret theSecret, D theData, P thePayload) throws DeedException;

    void setFailed(DeedSecret theSecret, DeedException theException);

}
