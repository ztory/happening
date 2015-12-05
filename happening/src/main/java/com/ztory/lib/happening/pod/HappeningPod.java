package com.ztory.lib.happening.pod;

import android.os.Handler;

import com.ztory.lib.happening.Happening;
import com.ztory.lib.happening.HappeningListener;
import com.ztory.lib.happening.RunObject;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for scoping any functionality and making it available for querying in a static and
 * decoupled way.
 * Created by jonruna on 21/08/15.
 */
public abstract class HappeningPod<Q, R> {

    public static final boolean ASYNC_TRUE = true, ASYNC_FALSE = false;

    private AtomicInteger mTaskIdGenerator = new AtomicInteger(0);

    private Executor mExecutor;

    /**
     * Designed for singleton-pattern, think twice before instantiating more than one instance of
     * the same HappeningPod-subclass.
     */
    protected HappeningPod(Executor theExecutor) {
        mExecutor = theExecutor;
    }

    protected final Executor podExecutor() {
        return mExecutor;
    }

    protected final int podGetUniqueTaskId() {
        return mTaskIdGenerator.incrementAndGet();
    }

    protected final <R1 extends R> PodR<R1> podResultCreate(int theMode, String theType, int theTaskId) {
        return new PodR<>(theMode, theType, theTaskId);
    }

    protected final <R1 extends R> PodR<R1> podResultSetPayload(PodR<R1> result, R1 payload) {
        result.setPayload(payload);
        podBroadcast(result);
        return result;
    }

    protected final <R1 extends R> PodR<R1> podResultSetException(PodR<R1> result, PodException e) {
        result.setException(e);
        podBroadcast(result);
        return result;
    }

    /**
     * Broadcasts a result to global-listeners of this HappeningPod instance.
     * Scope is private, since this should only be called when setting a PodR.payload or
     * PodR.PodException.
     * @param result the result to be broadcasted
     * @param <R1> subclass of R
     */
    private <R1 extends R> void podBroadcast(PodR<R1> result) {
        Happening.sendEvent(
                Happening.GROUP_ID_GLOBAL,
                podEventNameBroadcast(getClass()),
                result
        );
    }

    /**
     * Subclasses implement this in order to provide a PodResult to the callers PodQuery
     * @param query query-data
     * @return result-data
     */
    public PodR<R> podResult(PodQ<Q> query) {
        throw new IllegalStateException(
                "podResult() method not implemented in subclass: " + getClass()
        );
    }

    public <R1 extends R> HappeningListener podAddListener(
            final PodCallback<PodR<R1>> callback,
            int... releaseGroupIds
    ) {
        return podAddListener(callback, null, releaseGroupIds);
    }

    public <R1 extends R> HappeningListener podAddListener(
            final PodCallback<PodR<R1>> callback,
            final Handler uiHandler,
            int... releaseGroupIds
    ) {

        RunObject wrappedCallback = new RunObject() {
            @Override
            public Object r(final Object result) {
                if (uiHandler != null) {
                    uiHandler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    callback.callback((PodR<R1>) result);
                                }
                            }
                    );
                }
                else {
                    callback.callback((PodR<R1>) result);
                }
                return null;
            }
        };

        HappeningListener addListener = new HappeningListener(
                wrappedCallback,
                podEventNameBroadcast(getClass()),
                Happening.GROUP_ID_GLOBAL,
                releaseGroupIds
        );

        Happening.addListener(
                addListener
        );

        return addListener;
    }

    public void podRemoveListener(HappeningListener removeListener) {
        Happening.removeListener(removeListener);
    }

    public void podRemoveAllListeners() {
        Happening.removeListeners(
                true,//ignoreGroupId
                -1,//groupId is ignored
                podEventNameBroadcast(getClass())
        );
    }

    private static <C extends HappeningPod> String podEventNameBroadcast(Class<C> clazz) {
        return Happening.getEventName(clazz, "broadcast");
    }

    private static final ThreadFactory sPodThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            return new Thread(r, "HappeningPod Thread #" + mCount.getAndIncrement());
        }
    };

    protected static ThreadPoolExecutor podCreateExecutor(int poolSizeMin, int poolSizeMax) {
        return new ThreadPoolExecutor(
                poolSizeMin,//CORE_POOL_SIZE,
                poolSizeMax,//MAX_POOL_SIZE,
                4,//KEEP_ALIVE
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                sPodThreadFactory
        );
    }

}
