//package com.ztory.lib.happening;
//
//import com.ztory.lib.happening.pod.PodCallback;
//import com.ztory.lib.happening.pod.PodQ;
//import com.ztory.lib.happening.pod.PodR;
//
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.concurrent.CountDownLatch;
//
///**
// * Tests for the HappeningPod-functionality, using the HappeningPodExample-implementation class
// * for testing.
// */
//public class HappeningPodTest extends TestCase {
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    public void test1SynchronousResult() throws Exception {
//
//        final int podMode = 1;//dont change value
//        final String podType = "HappeningPodTest TYPE!!!!";
//        final String podPayload = "String as payload?!";
//        final String podKey1 = "key_1";
//        final double podVal1 = 2.99;
//
//        PodQ<String> podQuery = PodQ.withPayload(podPayload)
//                .setMode(podMode)
//                .setType(podType)
//                .put(podKey1, podVal1);
//
//        final String expectedToString =
//                "[" +
//                "Mode was 1!" + ", " +
//                "Type is: " + podType + ", " +
//                "Payload is: " + podPayload +
//                "]";
//
//        //Query the HappeningPod instance, get a result object back
//        PodR<ArrayList<String>> result = HappeningPodExample.get().podResult(podQuery);
//
//        assertEquals(podMode, result.getMode());
//        assertEquals(podType, result.getType());
//        assertEquals(true, result.isFinished());
//        assertNotNull(result.get());
//        assertEquals(expectedToString, result.get().toString());
//    }
//
//    public void test2BroadcastListener() throws Exception {
//
//        final CountDownLatch callbackLatch = new CountDownLatch(1);
//
//        HappeningListener podListener = HappeningPodExample.get().podAddListener(
//                new PodCallback<PodR<ArrayList<String>>>() {
//                    @Override
//                    public void callback(PodR<ArrayList<String>> result) {
//
//                        assertEquals(HappeningPodExample.MAGAZINE_FIND_ALL, result.getMode());
//                        assertEquals("magazine TYPE", result.getType());
//                        assertEquals(true, result.isFinished());
//                        assertNotNull(result.get());
//
//                        assertEquals(
//                                "[DONE processing findAllMagazines() call!]",
//                                result.get().toString()
//                        );
//
//                        HappeningPodExample.get().podRemoveAllListeners();
//
//                        callbackLatch.countDown();
//                    }
//                }
//        );
//
//        //Query the HappeningPod instance, get a result object back
//        PodR<ArrayList<String>> result = HappeningPodExample.get().findAllMagazines();
//
//        assertEquals(HappeningPodExample.MAGAZINE_FIND_ALL, result.getMode());
//        assertEquals("magazine TYPE", result.getType());
//        assertEquals(false, result.isFinished());
//        assertNull(result.get());
//
//        callbackLatch.await();
//
//        HappeningPodExample.get().podRemoveListener(podListener);
//
//        // After callbackLatch.await(); then the result should returned from result.get()
//        assertEquals(
//                "[DONE processing findAllMagazines() call!]",
//                result.get().toString()
//        );
//    }
//
//    public void test3ResultListener() throws Exception {
//
//        final CountDownLatch callbackLatch = new CountDownLatch(1);
//
//        //Query the HappeningPod instance, get a result object back
//        PodR<ArrayList<String>> result = HappeningPodExample.get().findAllMagazines();
//
//        assertEquals(HappeningPodExample.MAGAZINE_FIND_ALL, result.getMode());
//        assertEquals("magazine TYPE", result.getType());
//        assertEquals(false, result.isFinished());
//        assertNull(result.get());
//
//        result.addListener(
//                new PodCallback<PodR<ArrayList<String>>>() {
//                    @Override
//                    public void callback(PodR<ArrayList<String>> result) {
//
//                        assertEquals(HappeningPodExample.MAGAZINE_FIND_ALL, result.getMode());
//                        assertEquals("magazine TYPE", result.getType());
//                        assertEquals(true, result.isFinished());
//                        assertNotNull(result.get());
//
//                        assertEquals(
//                                "[DONE processing findAllMagazines() call!]",
//                                result.get().toString()
//                        );
//
//                        callbackLatch.countDown();
//                    }
//                },
//                null
//        );
//
//        callbackLatch.await();
//
//        // After callbackLatch.await(); then the result should returned from result.get()
//        assertEquals(
//                "[DONE processing findAllMagazines() call!]",
//                result.get().toString()
//        );
//    }
//
//}
