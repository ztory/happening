package com.ztory.lib.happening.pod;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * When calling DataPodRes.setSuccess() or DataPodRes.setFailed() an instance of DataPodSecret
 * is required, this is to prevent the calling of these methods from outside a DataPod class, only
 * DataPod instances have access to DataPodSecret instances.
 * Created by jonruna on 28/12/15.
 */
public final class DataPodSecret {

    private static AtomicInteger sSecretIdGenerator = new AtomicInteger(0);

    final int id;

    protected DataPodSecret() {
        id = sSecretIdGenerator.incrementAndGet();
    }

}
