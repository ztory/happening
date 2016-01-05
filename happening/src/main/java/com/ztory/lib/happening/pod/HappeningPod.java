package com.ztory.lib.happening.pod;

import android.os.Handler;

import com.ztory.lib.happening.Happening;
import com.ztory.lib.happening.HappeningListener;
import com.ztory.lib.happening.RunObject;
import com.ztory.lib.happening.result.Deed;
import com.ztory.lib.happening.result.DeedCallback;
import com.ztory.lib.happening.result.DeedException;
import com.ztory.lib.happening.result.DeedSecret;
import com.ztory.lib.happening.result.DeedSetter;
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
     * Create a PodDeed on the calling thread, if Deed.isFinished() == TRUE after this method is
     * executed then the Deed will be returned to the caller immediately without executing the
     * podProcess() method.
     * It is safe to do any operation and cause any Exception in this method,
     * since the Exception will be caught and returned in the Deed.
     * @param query the query object
     * @param <P> the parameterized payload
     * @param <Q> TypedMap<String, ?> & TypedPayload<G>
     * @return a PodDeed instance
     * @throws DeedException
     */
    protected abstract <P, Q extends TypedMap<String, ?> & TypedPayload<P>>
    DeedSetter<D, P> podCreateResult(Q query) throws DeedException;

    /**
     * This method processes the query and generates the result-Deed data and payload.
     * This method will be executed on a background-thread if this Pod was created with an
     * Executor instance, and the query is run with async==TRUE, otherwise this method will
     * be executed on the calling thread.
     * It is safe to do any operation and cause any Exception in this method,
     * since the Exception will be caught and returned in the Deed.
     * @param query the query object
     * @param result the result object
     * @param <P> the parameterized payload
     * @param <Q> TypedMap<String, ?> & TypedPayload<G>
     * @throws DeedException
     */
    protected abstract <P, Q extends TypedMap<String, ?> & TypedPayload<P>>
    void podProcess(Q query, DeedSetter<D, P> result) throws DeedException;

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
        } catch (Exception e) {
            result.setFailed(
                    podSecret(),
                    new DeedException(e)
            );
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
                query.typed(TypedMap.ASYNC, mAsyncDefault),
                query
        );
    }

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
            return queryExceptionResult;
        } catch (Exception e) {
            DeedSetter<D, P> queryExceptionResult = new PodResult<>(this, -1);
            queryExceptionResult.setFailed(
                    podSecret(),
                    new DeedException(e)
            );
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
