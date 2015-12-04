package com.ztory.lib.happening;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class used to register listeners in the Happening class.
 * Created by jonruna on 01/12/15.
 */
public class HappeningListener {

    private static final AtomicInteger uidAtomInt = new AtomicInteger(0);

    public final int uid;
    public final int listenGroupId;
    private final int[] releaseGroupIds;
    public final String eventName;
    public final RunObject listenerAsyncRun;
    public final Handler postToUiHandler;

    public HappeningListener(
            RunObject theListenerAsyncRun,
            String theEventName,
            int theListenGroupId,
            int... theReleaseGroupIds
    ) {
        this(
                theListenerAsyncRun,
                null,
                theEventName,
                theListenGroupId,
                theReleaseGroupIds
        );
    }

    /**
     * Create a listener that can be used to listen to events in the Happening class.
     * If no theReleaseGroupIds are supplied, then an array containing only theListenGroupId
     * will be created. If you want to listen on a groupId but do not want to release on it,
     * consider supplying GROUP_ID_DO_NOT_RELEASE as the theReleaseGroupIds value.
     * @param theListenerAsyncRun the callback interface
     * @param thePostToUiHandler if not null then callback will be posted to this handler
     * @param theEventName the event name to listen to
     * @param theListenGroupId the groupId to listen to
     * @param theReleaseGroupIds the groupIds that this listener will release on
     */
    public HappeningListener(
            RunObject theListenerAsyncRun,
            Handler thePostToUiHandler,
            String theEventName,
            int theListenGroupId,
            int... theReleaseGroupIds
    ) {
        uid = uidAtomInt.incrementAndGet();
        listenerAsyncRun = theListenerAsyncRun;
        postToUiHandler = thePostToUiHandler;
        eventName = theEventName;
        listenGroupId = theListenGroupId;

        if (theReleaseGroupIds.length == 0) {
            releaseGroupIds = new int[] { listenGroupId };
        }
        else {
            releaseGroupIds = theReleaseGroupIds;
        }
    }

    public boolean releaseOnGroupId(int groupIdToRelease) {
        for (int iterGroupId : releaseGroupIds) {
            if (iterGroupId == groupIdToRelease) {
                return true;
            }
        }
        return false;
    }

    public HappeningListener startListening() {
        Happening.addListener(this);
        return this;
    }

    public HappeningListener endListening() {
        Happening.removeListener(this);
        return this;
    }

}
