package com.ztory.lib.happening;

import com.ztory.lib.happening.deed.Deed;
import com.ztory.lib.happening.deed.DeedCallback;
import com.ztory.lib.happening.typed.Slab;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

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

    public void test1SynchronousResult() throws Exception {

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

    public void test2BroadcastListener() throws Exception {

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

    public void test3ResultListener() throws Exception {

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
