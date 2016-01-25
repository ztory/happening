package com.ztory.lib.happening;

import com.ztory.lib.happening.deed.Deed;
import com.ztory.lib.happening.deed.DeedCallback;
import com.ztory.lib.happening.typed.Slab;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests for the HappeningPod-functionality, using the HappeningPodExample-implementation class
 * for testing.
 */
public class HappeningPodTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParallelExecution() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(4);

        Deed<ArrayList<String>, Void> deed1, deed2, deed3, deed4;

        deed1 = HappeningPodExample.get().podMagazineFindAll(countDownLatch);
        deed2 = HappeningPodExample.get().podMagazineFindAll(countDownLatch);
        deed3 = HappeningPodExample.get().podMagazineFindAll(countDownLatch);

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        assertFalse(deed1.isFinished());
        assertFalse(deed2.isFinished());
        assertFalse(deed3.isFinished());

        deed4 = HappeningPodExample.get().podMagazineFindAll(countDownLatch);

        //countDownLatch.await();
        countDownLatch.await(4000, TimeUnit.MILLISECONDS);

        assertTrue(deed1.isFinished());
        assertTrue(deed2.isFinished());
        assertTrue(deed3.isFinished());
        assertTrue(deed4.isFinished());


    }

    public void testSynchronousResult() throws Exception {

        final int podMode = 16;
        final String podType = "HappeningPodTest TYPE!!!!";
        final String podPayload = "String as payload?!";
        final String podKey1 = "key_1";
        final double podVal1 = 2.99;

        final String expectedToString =
                "[" +
                "Mode was " + podMode + "!" + ", " +
                "Type is: [" + podType + "], " +
                "Payload is: " + podPayload +
                "]";

        Deed<ArrayList<String>, Void> result = HappeningPodExample.get().pod(
                HappeningPodExample.ASYNC_FALSE,
                new Slab<Void>()
                        .putSlab(Slab.MODE, podMode)
                        .putSlab(Slab.TYPE, podType)
                        .putSlab(Slab.PAYLOAD, podPayload)
                        .putSlab(podKey1, podVal1)
        );

        assertEquals(true, result.isFinished());
        assertNotNull(result.getData());
        assertEquals(expectedToString, result.getData().toString());
    }

    public void testBroadcastListener() throws Exception {

        final CountDownLatch callbackLatch = new CountDownLatch(1);

        HappeningPodExample.get().podAddListener(
                new DeedCallback<Deed<ArrayList<String>, ?>>() {
                    @Override
                    public void callback(Deed<ArrayList<String>, ?> result) {

                        assertEquals(true, result.isFinished());
                        assertNotNull(result.getData());

                        assertEquals(
                                "[DONE processing findAllMagazines() call!]",
                                result.getData().toString()
                        );

                        HappeningPodExample.get().podRemoveAllListeners();

                        callbackLatch.countDown();
                    }
                }
        );

        Deed<ArrayList<String>, Void> result = HappeningPodExample.get().pod(
                HappeningPodExample.ASYNC_TRUE,
                new Slab<Void>()
                        .putSlab(Slab.MODE, HappeningPodExample.MAGAZINE_FIND_ALL)
        );

        callbackLatch.await();

        // After callbackLatch.await(); then the result should returned from result.get()
        assertEquals(
                "[DONE processing findAllMagazines() call!]",
                result.getData().toString()
        );
    }

    public void testResultListener() throws Exception {

        final CountDownLatch callbackLatch = new CountDownLatch(1);

        Deed<ArrayList<String>, Void> result = HappeningPodExample.get().pod(
                HappeningPodExample.ASYNC_TRUE,
                new Slab<Void>()
                        .putSlab(Slab.MODE, HappeningPodExample.MAGAZINE_FIND_ALL)
        );

        result.addListener(
                new DeedCallback<Deed<ArrayList<String>, Void>>() {
                    @Override
                    public void callback(Deed<ArrayList<String>, Void> result) {

                        assertEquals(true, result.isFinished());
                        assertNotNull(result.getData());

                        assertEquals(
                                "[DONE processing findAllMagazines() call!]",
                                result.getData().toString()
                        );

                        callbackLatch.countDown();
                    }
                }
        );

        callbackLatch.await();

        // After callbackLatch.await(); then the result should returned from result.get()
        assertEquals(
                "[DONE processing findAllMagazines() call!]",
                result.getData().toString()
        );
    }

}
