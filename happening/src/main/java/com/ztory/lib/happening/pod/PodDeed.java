package com.ztory.lib.happening.pod;

import com.ztory.lib.happening.result.Deed;
import com.ztory.lib.happening.result.DeedException;

/**
 * Created by jonruna on 30/12/15.
 */
public interface PodDeed<D, P> extends Deed<D, P> {

    void setSuccess(PodSecret theSecret, D theData) throws DeedException;

    void setSuccess(PodSecret theSecret, D theData, P thePayload) throws DeedException;

    void setFailed(PodSecret theSecret, DeedException theException);

}
