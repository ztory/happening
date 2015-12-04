package com.ztory.lib.happening;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Tests for the AppPod-functionality, using the AppPodExample-implementation class for testing.
 */
public class HappeningTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSingleListener() {

        int eventGroupId = Happening.getUniqueActivityId();
        String eventName = Happening.getEventName(getClass(), "test_listener_1");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        countDownLatch.countDown();
                        return null;
                    }
                },
                eventName,
                eventGroupId
        ).startListening();

        Happening.sendEvent(eventGroupId, eventName);

        assertEquals(0, countDownLatch.getCount());

        Happening.removeListeners(eventGroupId);

        assertNull(Happening.sendEvent(eventGroupId, eventName));
    }

    public void testSingleGlobalListener() {

        String eventName = Happening.getEventName(getClass(), "test_listener_1");

        assertNull(
                "Exisiting GROUP_ID_GLOBAL listener on [" + eventName + "] already registered!",
                Happening.sendEvent(eventName)
        );

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        HappeningListener globalListener = new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        countDownLatch.countDown();
                        return null;
                    }
                },
                eventName,
                Happening.GROUP_ID_GLOBAL
        ).startListening();

        Happening.sendEvent(eventName);

        assertEquals(0, countDownLatch.getCount());

        globalListener.endListening();

        assertNull(Happening.sendEvent(eventName));
    }

    public void testMultipleListener() {

        int eventGroupId = Happening.getUniqueCustomGroupId();
        String eventName = Happening.getEventName(getClass(), "test_listener_2");

        final CountDownLatch countDownLatch = new CountDownLatch(7);

        //Listener #1
        HappeningListener listenerOne = new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        countDownLatch.countDown();
                        return null;
                    }
                },
                eventName,
                eventGroupId
        ).startListening();

        //Listener #2
        new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        countDownLatch.countDown();
                        return null;
                    }
                },
                eventName,
                eventGroupId
        ).startListening();

        //Listener #3
        new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        countDownLatch.countDown();
                        return null;
                    }
                },
                eventName,
                eventGroupId
        ).startListening();

        //Listener #4
        new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        countDownLatch.countDown();
                        return null;
                    }
                },
                eventName,
                eventGroupId
        ).startListening();

        Happening.sendEvent(eventGroupId, eventName);
        assertEquals(3, countDownLatch.getCount());

        listenerOne.endListening();

        Happening.sendEvent(eventGroupId, eventName);
        assertEquals(0, countDownLatch.getCount());

        Happening.removeListeners(eventGroupId);

        final CountDownLatch countDownLatchNotCalled = new CountDownLatch(1);

        Happening.sendEvent(eventGroupId, eventName);

        assertEquals(1, countDownLatchNotCalled.getCount());
    }

    public void testMultiplePayloadListener() {

        int eventGroupId = Happening.getUniqueCustomGroupId();
        String eventName = Happening.getEventName(getClass(), "test_listener_3");

        final String callbackDataOne = "HELLO";
        final String callbackDataTwo = "WORLD";

        //Listener #1
        new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        return callbackDataOne;
                    }
                },
                eventName,
                eventGroupId
        ).startListening();

        //Listener #2
        new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        return callbackDataTwo;
                    }
                },
                eventName,
                eventGroupId
        ).startListening();

        ArrayList<Object> listenerReturnData = Happening.sendEvent(eventGroupId, eventName);

        assertEquals(2, listenerReturnData.size());
        assertEquals(callbackDataOne, listenerReturnData.get(0));
        assertEquals(callbackDataTwo, listenerReturnData.get(1));

        Happening.removeListeners(eventName);

        assertNull(Happening.sendEvent(eventGroupId, eventName));
    }

    public void testStress1() {

        int eventGroupId = Happening.getUniqueCustomGroupId();
        String eventName = Happening.getEventName(getClass(), "test_listener_4");

        int listenerCount = 10000;

        final CountDownLatch countDownLatch = new CountDownLatch(listenerCount);

        for (int i = 0; i < listenerCount; i++) {
            new HappeningListener(
                    new RunObject() {
                        @Override
                        public Object r(Object o) {
                            countDownLatch.countDown();
                            return null;
                        }
                    },
                    eventName,
                    eventGroupId
            ).startListening();
        }

        assertEquals(listenerCount, Happening.sendEvent(eventGroupId, eventName).size());

        Happening.removeListeners(eventGroupId, eventName);

        assertNull(Happening.sendEvent(eventGroupId, eventName));
    }

    public void testStress2() {

        int eventGroupId = Happening.getUniqueCustomGroupId();
        String eventName = Happening.getEventName(getClass(), "test_listener_5");

        int listenerCount = 10000;

        final CountDownLatch countDownLatch = new CountDownLatch(listenerCount);

        new HappeningListener(
                new RunObject() {
                    @Override
                    public Object r(Object o) {
                        countDownLatch.countDown();
                        return null;
                    }
                },
                eventName,
                eventGroupId
        ).startListening();

        for (int i = 0; i < listenerCount; i++) {
            Happening.sendEvent(eventGroupId, eventName);
        }

        assertEquals(0, countDownLatch.getCount());

        Happening.removeListeners(eventGroupId, eventName);

        assertNull(Happening.sendEvent(eventGroupId, eventName));
    }

    public void testStress1and2times100() {

        for (int i = 0; i < 100; i++) {
            testStress1();
            testStress2();
        }
    }

}
