package com.ztory.lib.happening;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Event hub that will handle adding and removing of listeners, explicitly or by eventName or
 * groupId. This is also the class that you call when you want to send an event to listeners of
 * that eventName / groupId combo. Listeners with listenId == GROUP_ID_GLOBAL will listen to all
 * groupIds of its eventName.
 * Created by jonruna on 01/12/15.
 */
@SuppressLint("UseSparseArrays")
public class Happening {

    public static final int
            GROUP_ID_GLOBAL = -1,
            GROUP_ID_DO_NOT_RELEASE = -2;

    private static final String
            EVENT_NAME_PREFIX = "Happening.Event.",
            EVENT_NAME_SEPARATOR = ".";

    private static final AtomicInteger
            sActivityIdAtomInt = new AtomicInteger(0),
            sGroupIdAtomInt = new AtomicInteger(-10);

    private static final Object sLockObj = new Object();

    private static final ConcurrentHashMap<String, HashMap<Integer, HappeningListener>> sListenerMap =
            new ConcurrentHashMap<>(100, 0.75f, 2);

    /**
     * Use this to aquire a unique int id to be used to scope events to an id.
     * @return a negative unique int id
     */
    public static int getUniqueCustomGroupId() {
        return sGroupIdAtomInt.decrementAndGet();
    }

    /**
     * Same as getUniqueCustomGroupId() but can be used to aquire positive int ids,
     * to separate ids that are used for Activity-instances for example.
     * @return a positive unique int id
     */
    public static int getUniqueActivityId() {
        return sActivityIdAtomInt.incrementAndGet();
    }

    /**
     * Helper method to generate a namespaced eventName based on Class and eventNameSuffix.
     * @param clazz Class that wants to be used for namespacing
     * @param eventNameSuffix Event name
     * @return an event name String
     */
    public static String getEventName(Class clazz, String eventNameSuffix) {
        return EVENT_NAME_PREFIX + clazz.getName() + EVENT_NAME_SEPARATOR + eventNameSuffix;
    }

    /**
     * Register an event listener
     * @param listener the listener to register
     */
    public static void addListener(final HappeningListener listener) {

        synchronized (sLockObj) {

            HashMap<Integer, HappeningListener> eventMap = sListenerMap.get(listener.eventName);

            if (eventMap == null) {
                eventMap = new HashMap<>();
                sListenerMap.put(listener.eventName, eventMap);
            }

            eventMap.put(listener.uid, listener);
        }

        if (HappeningLog.LOG_ENABLED) {
            HappeningLog.log(Happening.class,
                    "ADDED listener, eventName", listener.eventName,
                    "uid", listener.uid
            );
        }
    }

    /**
     * Remove all listeners that will release on eventGroupId for each of the the eventNames.
     * @param eventGroupId groupId used to check if a listener will release on
     * @param eventNames eventNames that will check its listeners for removal
     */
    public static void removeListeners(
            final int eventGroupId,
            final String... eventNames
    ) {
        removeListeners(false, eventGroupId, eventNames);
    }

    /**
     * Will remove all listeners on all events in the eventNames array.
     * @param eventNames the eventNames to remove listeners from
     */
    public static void removeListeners(final String... eventNames) {
        removeListeners(true, -1, eventNames);
    }

    /**
     * Remove all listeners that will release on eventGroupId for each of the the eventNames.
     * If ignoreGroupId==true then all listeners that listen to any of the eventNames will be
     * removed.
     * @param ignoreGroupId if true then only eventNames are taken into consideration for removal
     * @param eventGroupId groupId used to check if a listener will release on
     * @param eventNames eventNames that will check its listeners for removal
     */
    public static void removeListeners(
            final boolean ignoreGroupId,
            final int eventGroupId,
            final String... eventNames
    ) {

        if (!ignoreGroupId && eventGroupId == GROUP_ID_DO_NOT_RELEASE) {
            throw new IllegalArgumentException(
                    "Can not release on GROUP_ID_DO_NOT_RELEASE (" + GROUP_ID_DO_NOT_RELEASE + ")."
            );
        }

        HashMap<Integer, HappeningListener> newEventMap;

        for (String iterEvent : eventNames) {

            synchronized (sLockObj) {

                if (sListenerMap.get(iterEvent) != null) {

                    newEventMap = getReleasedEventMap(ignoreGroupId, eventGroupId, iterEvent);

                    if (newEventMap != null) {
                        sListenerMap.put(iterEvent, newEventMap);
                    }
                    else {
                        sListenerMap.remove(iterEvent);
                    }
                }
            }

            if (HappeningLog.LOG_ENABLED) {
                HappeningLog.log(Happening.class,
                        "REMOVED all listeners for eventName", iterEvent,
                        " in groudId", eventGroupId
                );
            }
        }
    }

    /**
     * Returns new HashMap<Integer, HappeningListener> instance with the listeners that are not
     * removed by parameters, will return null if no listeners are left after operation.
     * @param ignoreGroupId if true then all listeners for eventName will be removed
     * @param eventGroupId used to determine releasing with listener.releaseOnGroupId(eventGroupId)
     * @param eventName the eventName of the listeners
     * @return new HashMap<Integer, HappeningListener> or null if no listeners left
     */
    private static HashMap<Integer, HappeningListener> getReleasedEventMap(
            boolean ignoreGroupId,
            int eventGroupId,
            String eventName
    ) {

        HashMap<Integer, HappeningListener> newEventMap = null;

        for (
                Entry<Integer, HappeningListener> iterEntry :
                sListenerMap.get(eventName).entrySet()
                ) {

            if (
                    !ignoreGroupId &&
                    !iterEntry.getValue().releaseOnGroupId(eventGroupId)
                    ) {

                if (newEventMap == null) {
                    newEventMap = new HashMap<>(20);
                }

                newEventMap.put(iterEntry.getKey(), iterEntry.getValue());
            }
        }

        return newEventMap;
    }

    /**
     * Will remove all listeners that have eventGroupId in their releaseId-array.
     * @param eventGroupId the eventGroupId to release
     */
    public static void removeListeners(final int eventGroupId) {

        if (eventGroupId == GROUP_ID_DO_NOT_RELEASE) {
            throw new IllegalArgumentException(
                    "Can not release on GROUP_ID_DO_NOT_RELEASE (" + GROUP_ID_DO_NOT_RELEASE + ")."
            );
        }

        synchronized (sLockObj) {

            Iterator<Entry<String, HashMap<Integer, HappeningListener>>> it;
            it = sListenerMap.entrySet().iterator();

            Entry<String, HashMap<Integer, HappeningListener>> iterPairs;

            while (it.hasNext()) {

                iterPairs = it.next();

                Iterator<Entry<Integer, HappeningListener>> innerIterator;
                innerIterator = iterPairs.getValue().entrySet().iterator();

                Entry<Integer, HappeningListener> innerIterPairs;

                while (innerIterator.hasNext()) {

                    innerIterPairs = innerIterator.next();

                    if (innerIterPairs.getValue().releaseOnGroupId(eventGroupId)) {

                        innerIterator.remove();

                        if (HappeningLog.LOG_ENABLED) {
                            HappeningLog.log(Happening.class,
                                    "REMOVED listener with uid", innerIterPairs.getValue().uid,
                                    "releaseGroupId", eventGroupId
                            );
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove a single listener
     * @param theHappeningListener the listener to remove
     */
    public static void removeListener(HappeningListener theHappeningListener) {

        if (theHappeningListener == null) {
            return;
        }

        removeListener(
                theHappeningListener.eventName,
                theHappeningListener.uid
        );
    }

    /**
     * Remove a single listener
     * @param eventName the listener eventName
     * @param eventUid the listener uid
     */
    public static void removeListener(final String eventName, final int eventUid) {
        synchronized (sLockObj) {

            HashMap<Integer, HappeningListener> eventMap = sListenerMap.get(eventName);

            if (eventMap != null) {
                eventMap.remove(Integer.valueOf(eventUid));

                if (HappeningLog.LOG_ENABLED) {
                    HappeningLog.log(Happening.class,
                            "REMOVED listener for eventName", eventName,
                            "eventUid", eventUid
                    );
                }
            }
        }
    }

    public static ArrayList<Object> sendEvent(String eventName) {
        return sendEvent(GROUP_ID_GLOBAL, eventName, null);
    }

    public static ArrayList<Object> sendEvent(int eventGroupId, String eventName) {
        return sendEvent(eventGroupId, eventName, null);
    }

    /**
     * Sends an event.
     * @param eventGroupId the groupId that will scope the event
     * @param eventName the name of the event
     * @param payload the payload of the event
     * @return an ArrayList with all return data from listeners that are not posting to a Handler.
     */
    public static ArrayList<Object> sendEvent(
            int eventGroupId,
            String eventName,
            final Object payload
    ) {

        final long execStartTs;

        if (HappeningLog.LOG_ENABLED) {
            execStartTs = System.currentTimeMillis();
        }
        else {
            execStartTs = 0;
        }

        HashMap<Integer, HappeningListener> eventMap = sListenerMap.get(eventName);

        if (HappeningLog.LOG_ENABLED) {
            HappeningLog.log(Happening.class,
                    "SEND", eventName,
                    "eventGroupId", eventGroupId,
                    "payload", payload
            );
        }

        if (eventMap == null || eventMap.size() == 0) {
            return null;//no listeners for eventName
        }

        ArrayList<HappeningListener> listeners;
        synchronized (sLockObj) {
            listeners = new ArrayList<>(eventMap.values().size());
            listeners.addAll(eventMap.values());
        }

        boolean foundListener = false;
        ArrayList<Object> synchronousReturnData = null;

        for (HappeningListener iterListener : listeners) {

            // Listener with listenGroupId == GROUP_ID_GLOBAL listens to events from all groupIds
            if (
                    iterListener.listenGroupId != GROUP_ID_GLOBAL &&
                    eventGroupId != iterListener.listenGroupId
                    ) {
                continue;
            }

            if (!foundListener) {
                foundListener = true;
                synchronousReturnData = new ArrayList<>(listeners.size());
            }

            if (HappeningLog.LOG_ENABLED) {
                HappeningLog.log(Happening.class,
                        "GET", eventName,
                        "listenGroupId", iterListener.listenGroupId,
                        "uid", iterListener.uid
                );
            }

            if (iterListener.postToUiHandler != null) {
                final HappeningListener finalIterListener = iterListener;
                Runnable uiRun = new Runnable() {
                    @Override
                    public void run() {
                        finalIterListener.listenerAsyncRun.r(payload);
                    }
                };
                iterListener.postToUiHandler.post(uiRun);
            }
            else {
                synchronousReturnData.add(iterListener.listenerAsyncRun.r(payload));
            }
        }

        if (HappeningLog.LOG_ENABLED) {
            HappeningLog.log(Happening.class,
                    "SEND", eventName,
                    "eventGroupId", eventGroupId,
                    "payload", payload,
                    "execution duration", System.currentTimeMillis() - execStartTs
            );
        }

        return synchronousReturnData;
    }

}
