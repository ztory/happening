package com.ztory.lib.happening.pod;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Result object used by HappeningPod to send data to classes that are listening to it
 * Created by jonruna on 23/08/15.
 */
public final class PodR<T> {

    private volatile boolean
            mFinished = false,
            mPayloadNotNull = false,
            mSuccessful = false,
            mAddedListeners = false;

    private volatile int
            mMode = -1,
            mTaskId = -1;

    private volatile String mType;

    private volatile T mPayload;
    private volatile PodException mException;

    private ArrayList<PodCallback<PodR<T>>> mListeners;
    private HashMap<PodCallback<PodR<T>>, Handler> mHandlerMap;

    protected PodR(int theMode, String theType, int theTaskId) {
        setMode(theMode);
        setType(theType);
        setTaskId(theTaskId);
    }

    protected PodR<T> setMode(int theMode) {
        mMode = theMode;
        return this;
    }

    protected PodR<T> setType(String theType) {
        mType = theType;
        return this;
    }

    protected PodR<T> setTaskId(int theTaskId) {
        mTaskId = theTaskId;
        return this;
    }

    protected PodR<T> setPayload(T thePayload) {

        if (mFinished) {
            throw new IllegalStateException(
                    "setPayload() or setException() already called, illegal to call both " +
                    "these methods or calling them more than once!"
            );
        }

        mPayload = thePayload;

        mPayloadNotNull = mPayload != null;

        mSuccessful = true;

        mFinished = true;

        notifyListeners();

        return this;
    }

    protected PodR<T> setException(PodException theException) {

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

        return this;
    }

    /**
     * Method is thread-safe. Is blocking if listeners have been added to this PodResult
     */
    private void notifyListeners() {

        if (!mAddedListeners) {
            return;
        }

        ArrayList<PodCallback<PodR<T>>> tempListeners;
        HashMap<PodCallback<PodR<T>>, Handler> tempHandlerMap;

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

        for (final PodCallback<PodR<T>> iterListener : tempListeners) {

            uiHandler = tempHandlerMap.get(iterListener);

            if (uiHandler != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                iterListener.callback(PodR.this);
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
    public synchronized void addListener(
            final PodCallback<PodR<T>> listener,
            final Handler uiHandler
    ) {

        // This will force notifyListeners() to synchronize on PodResult.this
        mAddedListeners = true;

        if (mFinished) {
            if (uiHandler != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                listener.callback(PodR.this);
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
    public synchronized void removeListener(PodCallback<PodR<T>> listener) {

        if (mListeners == null) {
            return;
        }

        mListeners.remove(listener);
        mHandlerMap.remove(listener);
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the typecasted payload, may be null even if isSuccessful() == true
     */
    public T get() {
        return mPayload;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the exception that caused the PodResult to fail
     */
    public PodException getException() {
        return mException;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the result type
     */
    public String getType() {
        return mType;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the result mode, -1 == not set
     */
    public int getMode() {
        return mMode;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the task-id that is generating the result, -1 == not set
     */
    public int getTaskId() {
        return mTaskId;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is successful, always false until isFinished() == true
     */
    public boolean isSuccessful() {
        return mSuccessful;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is successful, returns the same as calling !isSuccessful()
     */
    public boolean isFailed() {
        return !isSuccessful();
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result has a non-null payload, always false until isFinished() == true
     */
    public boolean hasPayload() {
        return mPayloadNotNull;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is finished for consumtion by the caller
     */
    public boolean isFinished() {
        return mFinished;
    }

}
