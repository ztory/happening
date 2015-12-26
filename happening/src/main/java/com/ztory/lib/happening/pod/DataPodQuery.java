package com.ztory.lib.happening.pod;

/**
 * Created by jonruna on 26/12/15.
 */
public interface DataPodQuery<Q, PT> {
    boolean isAsync();
    Q getQueryObject();
}
