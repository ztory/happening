package com.ztory.lib.happening.pod;

import android.os.Handler;

import com.ztory.lib.happening.Happening;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jonruna on 26/12/15.
 */
public abstract class DataPodRes<D, P> {

    /**
     * Subclasses can override this method if they want to do additional grooming of data
     * before success is set to TRUE. This method will only be called when setSuccess() is called.
     */
    protected void onSuccess() {

    }

    private final String mDataPodEventName;
    private final int mTaskId;

    private volatile boolean
            mFinished = false,
            mSuccessful = false,
            mAddedListeners = false;

    private volatile D mData;
    private volatile P mPayload;
    private volatile PodException mException;

    private ArrayList<PodCallback<DataPodRes<D, P>>> mListeners;
    private HashMap<PodCallback<DataPodRes<D, P>>, Handler> mHandlerMap;

    protected DataPodRes(DataPod theDataPod, int theTaskId) {
        mDataPodEventName = theDataPod.podEventNameBroadcast();
        mTaskId = theTaskId;
    }

    public final void setSuccess(D theData) {
        setSuccess(theData, null);
    }

    public final void setSuccess(D theData, P thePayload) {

        if (mFinished) {
            throw new IllegalStateException(
                    "setPayload() or setException() already called, illegal to call both " +
                    "these methods or calling them more than once!"
            );
        }

        mData = theData;

        mPayload = thePayload;

        onSuccess();

        mSuccessful = true;

        mFinished = true;

        notifyListeners();

        Happening.sendEvent(
                Happening.GROUP_ID_GLOBAL,
                mDataPodEventName,
                DataPodRes.this
        );
    }

    public final void setFailed(PodException theException) {

        if (mFinished) {
            throw new IllegalStateException(
                    "setPayload() or setException() already called, illegal to call both " +
                    "these methods or calling them more than once!"
            );
        }

        mException = theException;

        mSuccessful = false;

        mFinished = true;

        notifyListeners();

        Happening.sendEvent(
                Happening.GROUP_ID_GLOBAL,
                mDataPodEventName,
                DataPodRes.this
        );
    }

    /**
     * Method is thread-safe. Is blocking if listeners have been added.
     */
    private void notifyListeners() {

        if (!mAddedListeners) {
            return;
        }

        ArrayList<PodCallback<DataPodRes<D, P>>> tempListeners;
        HashMap<PodCallback<DataPodRes<D, P>>, Handler> tempHandlerMap;

        synchronized (this) {
            if (mListeners == null) {
                return;
            }

            tempListeners = mListeners;
            tempHandlerMap = mHandlerMap;

            mListeners = null;
            mHandlerMap = null;
        }

        Handler uiHandler;

        for (final PodCallback<DataPodRes<D, P>> iterListener : tempListeners) {

            uiHandler = tempHandlerMap.get(iterListener);

            if (uiHandler != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                iterListener.callback(DataPodRes.this);
                            }
                        }
                );
            }
            else {
                iterListener.callback(this);
            }
        }
    }

    /**
     * Method is thread-safe AND blocking
     * Add a PodCallback-listener that will be called when the isFinished() == true, will be called
     * immediately if isFinished() is true when calling addListener()
     */
    public final synchronized void addListener(final PodCallback<DataPodRes<D, P>> listener) {
        addListener(listener, null);
    }

    /**
     * Method is thread-safe AND blocking
     * Add a PodCallback-listener that will be called when the isFinished() == true, will be called
     * immediately if isFinished() is true when calling addListener()
     */
    public final synchronized void addListener(
            final PodCallback<DataPodRes<D, P>> listener,
            final Handler uiHandler
    ) {

        // This will force notifyListeners() to synchronize on this instance
        mAddedListeners = true;

        if (mFinished) {
            if (uiHandler != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                listener.callback(DataPodRes.this);
                            }
                        }
                );
            }
            else {
                listener.callback(this);
            }
            return;
        }

        if (mListeners == null) {
            mListeners = new ArrayList<>(1);
            mHandlerMap = new HashMap<>(1);
        }
        mListeners.add(listener);

        if (uiHandler != null) {
            mHandlerMap.put(listener, uiHandler);
        }
    }

    /**
     * Method is thread-safe AND blocking
     * Remove a previously added PodCallback-listener
     */
    public final synchronized void removeListener(PodCallback<DataPodRes<D, P>> listener) {

        if (mListeners == null) {
            return;
        }

        mListeners.remove(listener);
        mHandlerMap.remove(listener);
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the typecasted data, may be null even if isSuccessful() == true
     */
    public final D getData() {
        return mData;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the typecasted payload, may be null even if isSuccessful() == true
     */
    public final P getPayload() {
        return mPayload;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the exception that caused the failure
     */
    public final PodException getException() {
        return mException;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the task-id that is generating the result, -1 == not set
     */
    public final int getTaskId() {
        return mTaskId;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is finished for consumtion by the caller
     */
    public final boolean isFinished() {
        return mFinished;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is successful, always false until isFinished() == true
     */
    public final boolean isSuccessful() {
        return mSuccessful;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is successful, returns the same as calling !isSuccessful()
     */
    public final boolean isFailed() {
        return !mSuccessful;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return same as calling get() != null
     */
    public final boolean hasData() {
        return mData != null;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return same as calling getPayload() != null
     */
    public final boolean hasPayload() {
        return mPayload != null;
    }

}
