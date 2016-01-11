package com.ztory.lib.happening.pod;

import android.os.Handler;

import com.ztory.lib.happening.Happening;
import com.ztory.lib.happening.HappeningListener;
import com.ztory.lib.happening.HappeningLog;
import com.ztory.lib.happening.RunObject;
import com.ztory.lib.happening.deed.Deed;
import com.ztory.lib.happening.deed.DeedCallback;
import com.ztory.lib.happening.deed.DeedException;
import com.ztory.lib.happening.deed.DeedSecret;
import com.ztory.lib.happening.deed.DeedSetter;
import com.ztory.lib.happening.typed.TypedMap;
import com.ztory.lib.happening.typed.TypedPayload;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for working with arbitrary data and functionality in a thread-safe way.
 * The idea is that all calls will pass through the pod() method, and subclasses will implement
 * its custom behaviour in the podCreateResult() and podProcess() methods.
 * Created by jonruna on 26/12/15.
 */
public abstract class HappeningPod<D> {

    /**
     * This method processes the query and generates the result-Deed data and payload.
     * This method will be executed on a background-thread if this Pod was created with an
     * Executor instance, and the query is run with async==TRUE, otherwise this method will
     * be executed on the calling thread.
     * It is safe to do any operation and cause any Exception in this method,
     * since the Exception will be caught and returned in a Deed.
     * @param query the query object
     * @param result the result object
     * @param <P> the parameterized payload
     * @param <Q> TypedMap<String, ?> & TypedPayload<G>
     * @throws Exception it is expected to throw a DeedException if query is malformed, or
     * if there was an error while generating the result.
     */
    protected abstract <P, Q extends TypedMap<String, ?> & TypedPayload<P>>
    void podProcess(Q query, DeedSetter<D, P> result) throws Exception;

    /**
     * Create a DeedSetter on the calling thread, if Deed.isFinished() == TRUE after this method is
     * executed then the Deed will be returned to the caller immediately without executing the
     * podProcess() method.
     * It is safe to do any operation and cause any Exception in this method,
     * since the Exception will be caught and returned in a Deed.
     * @param query the query object
     * @param <P> the parameterized payload
     * @param <Q> TypedMap<String, ?> & TypedPayload<G>
     * @return a PodDeed instance
     * @throws Exception it is expected to throw a DeedException if query is malformed
     */
    protected <P, Q extends TypedMap<String, ?> & TypedPayload<P>>
    DeedSetter<D, P> podCreateResult(Q query) throws Exception {
        return new PodResult<>(this, podGetUniqueTaskId());
    }

    /**
     * Override this method in subclasses to react to exceptions caught by HappeningPod.
     */
    protected void podOnException(Exception e) {

        if (HappeningLog.LOG_ENABLED) {
            if (e instanceof DeedException) {
                HappeningLog.log(getClass(),
                        e.getClass().getSimpleName() + " | code", ((DeedException) e).getCode(),
                        "e.getMessage()", e.getMessage()
                );
            }
            else {
                HappeningLog.log(getClass(),
                        "e.getClass().getSimpleName()", e.getClass().getSimpleName(),
                        "e.getMessage()", e.getMessage()
                );
            }
        }
    }

    protected final <P, Q extends TypedMap<String, ?> & TypedPayload<P>> void podSafeProcess(
            Q query,
            DeedSetter<D, P> result
    ) {

        try {
            podProcess(query, result);
        } catch (DeedException e) {
            result.setFailed(
                    podSecret(),
                    e
            );
            podOnException(e);
        } catch (Exception e) {
            result.setFailed(
                    podSecret(),
                    new DeedException(e)
            );
            podOnException(e);
        }

        if (!result.isFinished()) {
            result.setFailed(
                    podSecret(),
                    new DeedException(
                            "podSafeExec() is done executing but result is NOT finished!"
                    )
            );
        }
    }

    public final <P, Q extends TypedMap<String, ?> & TypedPayload<P>> Deed<D, P> pod(
            final Q query
    ) {
        return pod(
                (query != null) ? query.typed(TypedMap.ASYNC, mAsyncDefault) : mAsyncDefault,
                query
        );
    }

    /**
     * General method for querying the HappeningPod instance. This will generate a
     * Deed<D, P> instance result from the data provided in the query object.
     * @param async if the caller wants to generate the result on bg-thread, provided that the
     *              HappeningPod instance supports bg-operations by having set an Executor.
     * @param query the query data that will be used to generate the result from
     * @param <P> the parameterized type of the returned Deed
     * @param <Q> the query type: extends TypedMap<String, ?> & TypedPayload<P>>
     * @return a Deed<D, P> result, if pod() is called with async==FALSE then
     * Deed.isFinished() is always true after calling this method.
     */
    public final <P, Q extends TypedMap<String, ?> & TypedPayload<P>> Deed<D, P> pod(
            final boolean async,
            final Q query
    ) {

        final DeedSetter<D, P> result;

        try {
            result = podCreateResult(query);

            if (result.isFinished()) {
                return result;
            }
        } catch (DeedException e) {
            DeedSetter<D, P> queryExceptionResult = new PodResult<>(this, -1);
            queryExceptionResult.setFailed(
                    podSecret(),
                    e
            );
            podOnException(e);
            return queryExceptionResult;
        } catch (Exception e) {
            DeedSetter<D, P> queryExceptionResult = new PodResult<>(this, -1);
            queryExceptionResult.setFailed(
                    podSecret(),
                    new DeedException(e)
            );
            podOnException(e);
            return queryExceptionResult;
        }

        if (mHasExecutor && async) {
            podExecutor().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            podSafeProcess(query, result);
                        }
                    }
            );
        }
        else {
            podSafeProcess(query, result);
        }

        return result;
    }

    public static final boolean ASYNC_TRUE = true, ASYNC_FALSE = false;

    private final AtomicInteger mTaskIdGenerator;

    private final String mEventNameBroadcast;

    private final DeedSecret mPodSecret;

    private final Executor mExecutor;

    protected final boolean mHasExecutor;

    protected final boolean mAsyncDefault;

    /**
     * Designed for singleton-pattern, think twice before instantiating more than one instance
     * of the same subclass.
     */
    protected HappeningPod(Executor theExecutor) {
        this(theExecutor, true);
    }

    /**
     * Designed for singleton-pattern, think twice before instantiating more than one instance
     * of the same subclass.
     */
    protected HappeningPod(Executor theExecutor, boolean theAsyncDefault) {

        mAsyncDefault = theAsyncDefault;
        mTaskIdGenerator = new AtomicInteger(0);
        mEventNameBroadcast = Happening.getEventName(getClass(), "broadcast");
        mPodSecret = new DeedSecret();

        mExecutor = theExecutor;
        mHasExecutor = mExecutor != null;
    }

    protected final DeedSecret podSecret() {
        return mPodSecret;
    }

    protected final Executor podExecutor() {
        return mExecutor;
    }

    protected final int podGetUniqueTaskId() {
        return mTaskIdGenerator.incrementAndGet();
    }

    public final String podEventNameBroadcast() {
        return mEventNameBroadcast;
    }

    public final void podBroadcast(Deed<D, ?> result) {
        Happening.sendEvent(
                Happening.GROUP_ID_GLOBAL,
                podEventNameBroadcast(),
                result
        );
    }

    public final HappeningListener podAddListener(
            final DeedCallback<Deed<D, ?>> callback,
            int... releaseGroupIds
    ) {
        return podAddListener(callback, null, releaseGroupIds);
    }

    public final HappeningListener podAddListener(
            final DeedCallback<Deed<D, ?>> callback,
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
                                    callback.callback((Deed<D, ?>) result);
                                }
                            }
                    );
                }
                else {
                    callback.callback((Deed<D, ?>) result);
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
