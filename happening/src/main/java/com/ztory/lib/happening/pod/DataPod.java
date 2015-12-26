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
 * Created by jonruna on 26/12/15.
 */
public abstract class DataPod<Q, R> {

    public static final boolean ASYNC_TRUE = true, ASYNC_FALSE = false;

    private AtomicInteger mTaskIdGenerator = new AtomicInteger(0);

    private Executor mExecutor;

    /**
     * Designed for singleton-pattern, think twice before instantiating more than one instance of
     * the same HappeningPod-subclass.
     */
    protected DataPod(Executor theExecutor) {
        mExecutor = theExecutor;
    }

    protected final Executor podExecutor() {
        return mExecutor;
    }

    protected final int podGetUniqueTaskId() {
        return mTaskIdGenerator.incrementAndGet();
    }

    protected final <PT> DataPodResult<R, PT> podResultCreate(
            int theMode,
            String theType,
            int theTaskId
    ) {
        return new DataPodResult<>(theMode, theType, theTaskId);
    }

    protected final <PT> DataPodResult<R, PT> podResultPayload(
            DataPodResult<R, PT> result,
            R payload
    ) {
        result.setPayload(payload);
        podBroadcast(result);
        return result;
    }

    protected final <PT> DataPodResult<R, PT> podResultPayload(
            DataPodResult<R, PT> result,
            R payload,
            PT parameterizedPayload
    ) {
        result.setPayload(payload, parameterizedPayload);
        podBroadcast(result);
        return result;
    }

    protected final <PT> DataPodResult<R, PT> podResultException(
            DataPodResult<R, PT> result,
            PodException e
    ) {
        result.setException(e);
        podBroadcast(result);
        return result;
    }

    /**
     * Broadcasts a result to global-listeners of this HappeningPod instance.
     * Scope is private, since this should only be called when setting a PodR.payload or
     * PodR.PodException.
     * @param result the result to be broadcasted
     */
    private <PT> void podBroadcast(DataPodResult<R, PT> result) {
        Happening.sendEvent(
                Happening.GROUP_ID_GLOBAL,
                podEventNameBroadcast(getClass()),
                result
        );
    }

    public abstract <PT> DataPodResult<R, PT> podOperation(DataPodQuery<Q, PT> query);

    public HappeningListener podAddListener(
            final PodCallback<DataPodResult<R, ?>> callback,
            int... releaseGroupIds
    ) {
        return podAddListener(callback, null, releaseGroupIds);
    }

    public HappeningListener podAddListener(
            final PodCallback<DataPodResult<R, ?>> callback,
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
                                    callback.callback((DataPodResult<R, ?>) result);
                                }
                            }
                    );
                }
                else {
                    callback.callback((DataPodResult<R, ?>) result);
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

    public static <Q> DataPodQuery<Q, Void> podCreateQuery(
            final boolean async,
            final Q queryObject
    ) {
        return new DataPodQuery<Q, Void>() {
            @Override
            public boolean isAsync() {
                return async;
            }

            @Override
            public Q getQueryObject() {
                return queryObject;
            }
        };
    }

    public static <Q, PT> DataPodQuery<Q, PT> podCreateQuery(
            final boolean async,
            final Q queryObject,
            Class<PT> parameterizedClazz
    ) {
        return new DataPodQuery<Q, PT>() {
            @Override
            public boolean isAsync() {
                return async;
            }

            @Override
            public Q getQueryObject() {
                return queryObject;
            }
        };
    }

    private static <C extends DataPod> String podEventNameBroadcast(Class<C> clazz) {
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
