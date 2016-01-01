package com.ztory.lib.happening.pod;

import android.os.Handler;

import com.ztory.lib.happening.Happening;
import com.ztory.lib.happening.HappeningListener;
import com.ztory.lib.happening.RunObject;
import com.ztory.lib.happening.typed.TypedMap;
import com.ztory.lib.happening.typed.TypedPayload;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for working with arbitrary data and/or functionality in a thread-safe way.
 * Created by jonruna on 26/12/15.
 */
public abstract class HappeningPod<D> {

    protected abstract <G, Q extends TypedMap<String, ?> & TypedPayload<G>>
    PodR<D, G> podCreateResult(Q query);

    protected abstract <G, Q extends TypedMap<String, ?> & TypedPayload<G>>
    void podProcess(Q query, PodR<D, G> result) throws PodException;

    protected final <G, Q extends TypedMap<String, ?> & TypedPayload<G>> void podSafeProcess(
            Q query,
            PodR<D, G> result
    ) {

        try {
            podProcess(query, result);
        } catch (PodException e) {
            result.setFailed(
                    podSecret(),
                    e
            );
        } catch (Exception e) {
            result.setFailed(
                    podSecret(),
                    new PodException(e)
            );
        }

        if (!result.isFinished()) {
            result.setFailed(
                    podSecret(),
                    new PodException(
                            "podSafeExec() is done executing but result is NOT finished!"
                    )
            );
        }
    }

    public final <G, Q extends TypedMap<String, ?> & TypedPayload<G>> PodR<D, G> podOperation(
            final Q query
    ) {
        return podOperation(
                query.typed(TypedMap.KEY_ASYNC, true),
                query
        );
    }

    public final <G, Q extends TypedMap<String, ?> & TypedPayload<G>> PodR<D, G> podOperation(
            final boolean async,
            final Q query
    ) {

        if (query == null) {
            PodR<D, G> queryNullResult = new PodResult<D, G>(this, -1) { };
            queryNullResult.setFailed(
                    podSecret(),
                    new PodException("query == null")
            );
            return queryNullResult;
        }

        final PodR<D, G> result = podCreateResult(query);

        if (result.isFinished()) {
            return result;
        }

        if (mHasExecutor && async) {

            Runnable bgRun = new Runnable() {
                @Override
                public void run() {
                    podSafeProcess(query, result);
                }
            };
            podExecutor().execute(bgRun);
        }
        else {
            podSafeProcess(query, result);
        }

        return result;
    }

    public static final boolean ASYNC_TRUE = true, ASYNC_FALSE = false;

    private final AtomicInteger mTaskIdGenerator;

    private final String mEventNameBroadcast;

    private final PodSecret mPodSecret;

    private final Executor mExecutor;

    protected final boolean mHasExecutor;

    /**
     * Designed for singleton-pattern, think twice before instantiating more than one instance
     * of the same subclass.
     */
    protected HappeningPod(Executor theExecutor) {

        mTaskIdGenerator = new AtomicInteger(0);
        mEventNameBroadcast = Happening.getEventName(getClass(), "broadcast");
        mPodSecret = new PodSecret();

        mExecutor = theExecutor;
        mHasExecutor = mExecutor != null;
    }

    protected final PodSecret podSecret() {
        return mPodSecret;
    }

    protected final Executor podExecutor() {
        return mExecutor;
    }

    protected final int podGetUniqueTaskId() {
        return mTaskIdGenerator.incrementAndGet();
    }

    public final HappeningListener podAddListener(
            final PodCallback<PodR<D, ?>> callback,
            int... releaseGroupIds
    ) {
        return podAddListener(callback, null, releaseGroupIds);
    }

    public final HappeningListener podAddListener(
            final PodCallback<PodR<D, ?>> callback,
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
                                    callback.callback((PodR<D, ?>) result);
                                }
                            }
                    );
                }
                else {
                    callback.callback((PodR<D, ?>) result);
                }
                return null;
            }
        };

        HappeningListener addListener = new HappeningListener(
                wrappedCallback,
                podEventNameBroadcast(),
                Happening.GROUP_ID_GLOBAL,
                releaseGroupIds
        );

        return addListener.startListening();
    }

    public final void podRemoveListener(HappeningListener removeListener) {
        Happening.removeListener(removeListener);
    }

    public final void podRemoveAllListeners() {
        Happening.removeListeners(
                true,//ignoreGroupId
                -1,//groupId is ignored
                podEventNameBroadcast()
        );
    }

    public final String podEventNameBroadcast() {
        return mEventNameBroadcast;
    }

    private static final ThreadFactory sPodThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            return new Thread(r, "DataPod Thread #" + mCount.getAndIncrement());
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
