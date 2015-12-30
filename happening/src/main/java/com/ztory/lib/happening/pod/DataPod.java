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
public abstract class DataPod<R extends PodR> {

    public static final boolean ASYNC_TRUE = true, ASYNC_FALSE = false;

    private AtomicInteger mTaskIdGenerator = new AtomicInteger(0);

    private String mEventNameBroadcast;

    private PodSecret mPodSecret;

    private Executor mExecutor;

    /**
     * Designed for singleton-pattern, think twice before instantiating more than one instance
     * of the same subclass.
     */
    protected DataPod(Executor theExecutor) {

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
            final PodCallback<R> callback,
            int... releaseGroupIds
    ) {
        return podAddListener(callback, null, releaseGroupIds);
    }

    public HappeningListener podAddListener(
            final PodCallback<R> callback,
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
                                    callback.callback((R) result);
                                }
                            }
                    );
                }
                else {
                    callback.callback((R) result);
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
