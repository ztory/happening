package com.ztory.lib.happening;

import com.ztory.lib.happening.deed.Deed;
import com.ztory.lib.happening.pod.HappeningPod;
import com.ztory.lib.happening.pod.PodResult;
import com.ztory.lib.happening.deed.DeedException;
import com.ztory.lib.happening.deed.DeedSetter;
import com.ztory.lib.happening.typed.Slab;
import com.ztory.lib.happening.typed.Typed;
import com.ztory.lib.happening.typed.TypedMap;
import com.ztory.lib.happening.typed.TypedPayload;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Example implementation class of HappeningPod
 * Created by jonruna on 21/08/15.
 */
public class HappeningPodExample extends HappeningPod<ArrayList<String>> {

    //FUNCTIONALITY HERE IS TESTED IN HappeningPodTest.java

    private static HappeningPodExample sInstance;

    public static HappeningPodExample get() {
        if (sInstance == null) {
            sInstance = new HappeningPodExample();
        }
        return sInstance;
    }

    public static volatile int magazineFindAllCount = 0;

    public static final int MAGAZINE_FIND_ALL = 44;

    protected HappeningPodExample() {
        super(podCreateExecutor(HappeningPodExample.class.getSimpleName(), 4));
    }

    public final Deed<ArrayList<String>, Void> podMagazineFindAll(CountDownLatch latch) {
        return pod(
                HappeningPodExample.ASYNC_TRUE,
                new Slab<Void>()
                        .putSlab(Slab.MODE, HappeningPodExample.MAGAZINE_FIND_ALL)
                        .putSlab(CountDownLatch.class.getName(), latch)
        );
    }

    @Override
    protected <P, Q extends TypedMap<String, ?> & TypedPayload<P>>
    DeedSetter<ArrayList<String>, P> podCreateResult(Q query) throws DeedException {
        return new PodResult<>(this, podGetUniqueTaskId());
    }

    @Override
    protected <P, Q extends TypedMap<String, ?> & TypedPayload<P>>
    void podProcess(Q query, DeedSetter<ArrayList<String>, P> result) throws DeedException {

        if (query.typed(TypedMap.MODE, -1) == MAGAZINE_FIND_ALL) {

            CountDownLatch latch = query.typed(CountDownLatch.class.getName());

            if (latch != null) {
                try {
                    latch.countDown();
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<String> listResult = new ArrayList<>();
            listResult.add("DONE processing findAllMagazines() call!");

            result.setSuccess(podSecret(), listResult);
        }
        else {
            ArrayList<String> listResult = new ArrayList<>();

            listResult.add("Mode was " + query.typed(TypedMap.MODE) + "!");

            listResult.add("Type is: [" + query.typed(TypedMap.TYPE) + "]");

            if (query.getPayload() == null) {
                listResult.add("Payload is null");
            }
            else if (!(query.getPayload() instanceof String)) {
                listResult.add("Payload is NOT String");
            }
            else if (Typed.get(query.getPayload(), "").length() == 0) {
                listResult.add("Payload is empty");
            }
            else {
                listResult.add("Payload is: " + query.getPayload());
            }

            result.setSuccess(podSecret(), listResult);
        }
    }

}
