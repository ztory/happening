package com.ztory.lib.happening;

import com.ztory.lib.happening.pod.HappeningPod;
import com.ztory.lib.happening.pod.PodQ;
import com.ztory.lib.happening.pod.PodR;

import java.util.ArrayList;

/**
 * Example implementation class of HappeningPod
 * Created by jonruna on 21/08/15.
 */
public class HappeningPodExample extends HappeningPod<String, ArrayList<String>> {

    //TODO FUNCTIONALITY HERE IS TESTED IN HappeningPodTest.java

    /*
    EXAMPLE USAGE:

        //Add listener to broadcast-events
        HappeningPodExample.podAddListener(
                new PodCallback<PodResult<ArrayList<String>>>() {
                    @Override
                    public void callback(PodResult<ArrayList<String>> result) {
                        Ton.get().displayAlert(
                                "Mode:\n" + result.getMode() +
                                "\nFinished:\n" + result.isFinished() +
                                "\nType:\n" + result.getType() +
                                "\nPayload:\n" + result.get().toString()
                        );
                    }
                },
                AppActivity.getUiHandler(),
                HappeningPodExample.class,
                getActivityId()
        );

        //Query the HappeningPod instance, get a result object back
        PodResult<ArrayList<String>> result = HappeningPodExample.get().podResult(
                PodQuery.withPayload("Payload of the year!")
                        .setMode(1)
                        .setType("dashboard TYPE")
                        .put("test_key", 2.99)
        );

        Ton.get().displayAlert(
                "Mode:\n" + result.getMode() +
                "\nFinished:\n" + result.isFinished() +
                "\nType:\n" + result.getType() +
                "\nPayload:\n" + result.get().toString()
        );

        //Query the HappeningPod instance, get a result object back
        result = HappeningPodExample.get().findAllMagazines();

        //Add listener on result object instance
        result.addListener(
                new PodCallback<PodResult<ArrayList<String>>>() {
                    @Override
                    public void callback(PodResult<ArrayList<String>> result) {
                        Ton.get().displayAlert(
                                "LISTENER ON PodResult<ArrayList<String>> instance EXECUTED!" +
                                "\nMode:\n" + result.getMode() +
                                "\nFinished:\n" + result.isFinished() +
                                "\nType:\n" + result.getType() +
                                "\nPayload:\n" + result.get()
                        );
                    }
                },
                AppActivity.getUiHandler()
        );

        Ton.get().displayAlert(
                "Mode:\n" + result.getMode() +
                "\nFinished:\n" + result.isFinished() +
                "\nType:\n" + result.getType() +
                "\nPayload:\n" + result.get()
        );
    */


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

    public PodR<ArrayList<String>> findAllMagazines() {
        return podResult(
                PodQ.withPayload("MAGAZINES + MAGAZINES = MAGAZINESMAGAZINES")
                        .setMode(MAGAZINE_FIND_ALL)
                        .setType("magazine TYPE")
                        .put("test_key", 44.44)
        );
    }

    public PodR<ArrayList<String>> podResult(PodQ<String> query) {

        if (query.getMode() == MAGAZINE_FIND_ALL) {
            return processMagazines(query);
        }

        return processDefault(query);
    }

    private PodR<ArrayList<String>> processMagazines(PodQ<String> query) {

        final PodR<ArrayList<String>> podResult = podResultCreate(
                query.getMode(),
                query.getType(),
                podGetUniqueTaskId()
        );

        Runnable bgProcess = new Runnable() {
            @Override
            public void run() {

                synchronized (this) {
                    try {
                        this.wait(400);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                ArrayList<String> threadResult = new ArrayList<>();
                threadResult.add("DONE processing findAllMagazines() call!");

                podResultSetPayload(podResult, threadResult);
            }
        };
        podExecutor().execute(bgProcess);

        return podResult;
    }

    private PodR<ArrayList<String>> processDefault(PodQ<String> query) {

        ArrayList<String> result = new ArrayList<>();

        switch (query.getMode()) {
            case 0:
                result.add("Mode was 0!");
                break;
            case 1:
                result.add("Mode was 1!");
                break;
            case 2:
                result.add("Mode was 2!");
                break;
        }

        if (query.getType() == null) {
            result.add("Type is null");
        }
        else if (query.getType().length() == 0) {
            result.add("Type is empty");
        }
        else {
            result.add("Type is: " + query.getType());
        }

        if (query.getPayload() == null) {
            result.add("Payload is null");
        }
        else if (query.getPayload().length() == 0) {
            result.add("Payload is empty");
        }
        else {
            result.add("Payload is: " + query.getPayload());
        }

        PodR<ArrayList<String>> returnResult = podResultCreate(
                query.getMode(),
                query.getType(),
                podGetUniqueTaskId()
        );

        return podResultSetPayload(returnResult, result);
    }

}
