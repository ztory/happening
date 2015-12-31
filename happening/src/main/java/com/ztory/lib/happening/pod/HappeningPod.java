package com.ztory.lib.happening.pod;

import android.os.Handler;

import com.ztory.lib.happening.Happening;
import com.ztory.lib.happening.HappeningListener;
import com.ztory.lib.happening.RunObject;
import com.ztory.lib.happening.typed.TypedMap;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for working with arbitrary data and/or functionality in a thread-safe way.
 * It is up to subclasses to determine if the returned data is generated on the calling thread
 * or on a background thread, but it is preferred to return a reference to the PodResult instance
 * even if the data is generated on a background thread, that way consumers can determine what
 * action they want to take when data is not ready for consumtion immediately.
 *
 * If this code was possible in Java:
 * <code>public R<G> operation(Q<G> queryObject)</code>
 * then it is a good idea to have all functionality pass through that method, so that it is
 * easy to follow the execution-flow for consumers of this class. And all other instance-methods
 * would be helper methods that called operation() with more specific arguments.
 *
 * Created by jonruna on 26/12/15.
 */
public abstract class HappeningPod<D> {

    protected abstract <G, Q extends PodPayload<G> & PodAsync & TypedMap<String, ?>>
    PodR<D, G> podCreateResult(Q query);

    protected abstract <G, Q extends PodPayload<G> & PodAsync & TypedMap<String, ?>>
    void podProcess(Q query, PodR<D, G> result) throws PodException;

    protected final <G, Q extends PodPayload<G> & PodAsync & TypedMap<String, ?>>
    void podSafeProcess(Q query, PodR<D, G> result) {

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

    public final <G, Q extends PodPayload<G> & PodAsync & TypedMap<String, ?>>
    PodR<D, G> podOperation(final Q query) {

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

        if (query.isAsync()) {

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

    private AtomicInteger mTaskIdGenerator = new AtomicInteger(0);

    private String mEventNameBroadcast;

    private PodSecret mPodSecret;

    private Executor mExecutor;

    /**
     * Designed for singleton-pattern, think twice before instantiating more than one instance
     * of the same subclass.
     */
    protected HappeningPod(Executor theExecutor) {

        mTaskIdGenerator = new AtomicInteger(0);
        mEventNameBroadcast = Happening.getEventName(getClass(), "broadcast");
        mPodSecret = new PodSecret();

        mExecutor = theExecutor;
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

    public HappeningListener podAddListener(
            final PodCallback<PodR<D, ?>> callback,
            int... releaseGroupIds
    ) {
        return podAddListener(callback, null, releaseGroupIds);
    }

    public HappeningListener podAddListener(
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

    public void podRemoveListener(HappeningListener removeListener) {
        Happening.removeListener(removeListener);
    }

    public void podRemoveAllListeners() {
        Happening.removeListeners(
                true,//ignoreGroupId
                -1,//groupId is ignored
                podEventNameBroadcast()
        );
    }

    public String podEventNameBroadcast() {
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
