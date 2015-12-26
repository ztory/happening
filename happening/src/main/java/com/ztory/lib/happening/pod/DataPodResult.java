package com.ztory.lib.happening.pod;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jonruna on 26/12/15.
 */
public final class DataPodResult<P, PO> {

    private volatile boolean
            mFinished = false,
            mSuccessful = false,
            mAddedListeners = false;

    private volatile int
            mMode = -1,
            mTaskId = -1;

    private volatile String mType;

    private volatile P mPayload;
    private volatile PO mParameterizedPayload;
    private volatile PodException mException;

    private ArrayList<PodCallback<DataPodResult<P, PO>>> mListeners;
    private HashMap<PodCallback<DataPodResult<P, PO>>, Handler> mHandlerMap;

    protected DataPodResult(int theMode, String theType, int theTaskId) {
        setMode(theMode);
        setType(theType);
        setTaskId(theTaskId);
    }

    protected DataPodResult<P, PO> setMode(int theMode) {
        mMode = theMode;
        return this;
    }

    protected DataPodResult<P, PO> setType(String theType) {
        mType = theType;
        return this;
    }

    protected DataPodResult<P, PO> setTaskId(int theTaskId) {
        mTaskId = theTaskId;
        return this;
    }

    protected DataPodResult<P, PO> setPayload(P thePayload) {
        return setPayload(thePayload, null);
    }

    protected DataPodResult<P, PO> setPayload(P thePayload, PO theParameterizedPayload) {

        if (mFinished) {
            throw new IllegalStateException(
                    "setPayload() or setException() already called, illegal to call both " +
                    "these methods or calling them more than once!"
            );
        }

        mPayload = thePayload;

        mParameterizedPayload = theParameterizedPayload;

        mSuccessful = true;

        mFinished = true;

        notifyListeners();

//        Happening.sendEvent(
//                Happening.GROUP_ID_GLOBAL,
//                podEventNameBroadcast(getClass()),
//                this
//        );

        return this;
    }

    protected DataPodResult<P, PO> setException(PodException theException) {

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

        ArrayList<PodCallback<DataPodResult<P, PO>>> tempListeners;
        HashMap<PodCallback<DataPodResult<P, PO>>, Handler> tempHandlerMap;

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

        for (final PodCallback<DataPodResult<P, PO>> iterListener : tempListeners) {

            uiHandler = tempHandlerMap.get(iterListener);

            if (uiHandler != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                iterListener.callback(DataPodResult.this);
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
            final PodCallback<DataPodResult<P, PO>> listener,
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
                                listener.callback(DataPodResult.this);
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
    public synchronized void removeListener(PodCallback<DataPodResult<P, PO>> listener) {

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
    public P get() {
        return mPayload;
    }


    public PO getParameterized() {
        return mParameterizedPayload;
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
        return mPayload != null;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is finished for consumtion by the caller
     */
    public boolean isFinished() {
        return mFinished;
    }

}
