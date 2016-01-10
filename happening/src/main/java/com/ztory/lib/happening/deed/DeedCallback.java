package com.ztory.lib.happening.deed;

/**
 * Callback interface for Deed instances
 * Created by jonruna on 21/08/15.
 */
public interface DeedCallback<R extends Deed> {
    void callback(R result);
}
