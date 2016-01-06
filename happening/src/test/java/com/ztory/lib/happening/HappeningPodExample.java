package com.ztory.lib.happening;

import com.ztory.lib.happening.pod.HappeningPod;
import com.ztory.lib.happening.pod.PodResult;
import com.ztory.lib.happening.result.DeedException;
import com.ztory.lib.happening.result.DeedSetter;
import com.ztory.lib.happening.typed.Typed;
import com.ztory.lib.happening.typed.TypedMap;
import com.ztory.lib.happening.typed.TypedPayload;

import java.util.ArrayList;

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

    public static final int MAGAZINE_FIND_ALL = 44;

    protected HappeningPodExample() {
        super(podCreateExecutor(0, 1));
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
