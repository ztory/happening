package com.ztory.lib.happening.pod;

/**
 * Callback interface for HappeningPod
 * Created by jonruna on 21/08/15.
 */
public interface PodCallback<R> {
    void callback(R result);
}
