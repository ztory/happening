package com.ztory.lib.happening.pod;

import android.os.Handler;

import com.ztory.lib.happening.deed.Deed;
import com.ztory.lib.happening.deed.DeedCallback;
import com.ztory.lib.happening.deed.DeedException;
import com.ztory.lib.happening.deed.DeedSecret;
import com.ztory.lib.happening.deed.DeedSetter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides thread-safe async-functionality for notifying listeners when data or
 * exceptions are ready for consumption.
 * Created by jonruna on 26/12/15.
 */
public class PodResult<D, P> implements DeedSetter<D, P> {

    /**
     * Subclasses can override this method if they want to do additional grooming of data
     * before success is set to TRUE. This method will only be called when setSuccess() is called.
     * @return a P instance, will only be set as this PodResult Payload if there was no payload
     * set in the call to setSuccess()
     * @throws DeedException
     */
    protected P onSuccess() throws DeedException {
        return null;
    }

    private final HappeningPod<D> mHappeningPod;

    private final int mTaskId;

    private volatile boolean
            mFinished = false,
            mSuccessful = false,
            mAddedListeners = false;

    private volatile D mData;
    private volatile P mPayload;
    private volatile DeedException mException;

    private ArrayList<DeedCallback<Deed<D, P>>> mListeners;
    private HashMap<DeedCallback<Deed<D, P>>, Handler> mHandlerMap;

    public PodResult(HappeningPod<D> thePod, int theTaskId) {

        mHappeningPod = thePod;

        if (mHappeningPod == null) {
            throw new IllegalArgumentException("thePod == null");
        }

        mTaskId = theTaskId;
    }

    @Override
    public final void setSuccess(DeedSecret theSecret, D theData) throws DeedException {
        setSuccess(theSecret, theData, null);
    }

    @Override
    public final void setSuccess(DeedSecret theSecret, D theData, P thePayload)
            throws DeedException
    {

        if (mFinished) {
            throw new IllegalStateException(
                    "setPayload() or setException() already called, illegal to call both " +
                    "these methods or calling them more than once!"
            );
        }
        else if (theSecret.hashCode() != mHappeningPod.podSecret().hashCode()) {
            throw new IllegalArgumentException(
                    "theSecret does not match mDataPod.podSecret()."
            );
        }

        mData = theData;

        mPayload = thePayload;

        if (mPayload == null) {
            mPayload = onSuccess();
        }
        else {
            onSuccess();
        }

        mSuccessful = true;

        mFinished = true;

        notifyListeners();

        mHappeningPod.podBroadcast(this);
    }

    @Override
    public final void setFailed(
            DeedSecret theSecret,
            DeedException theException
    ) {

        if (mFinished) {
            throw new IllegalStateException(
                    "setPayload() or setException() already called, illegal to call both " +
                    "these methods or calling them more than once!"
            );
        }
        else if (theSecret.hashCode() != mHappeningPod.podSecret().hashCode()) {
            throw new IllegalArgumentException(
                    "theSecret does not match mDataPod.podSecret()."
            );
        }

        mException = theException;

        mSuccessful = false;

        mFinished = true;

        notifyListeners();

        mHappeningPod.podBroadcast(this);
    }

    /**
     * Method is thread-safe. Is blocking if listeners have been added.
     */
    private void notifyListeners() {

        if (!mAddedListeners) {
            return;
        }

        ArrayList<DeedCallback<Deed<D, P>>> tempListeners;
        HashMap<DeedCallback<Deed<D, P>>, Handler> tempHandlerMap;

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

        for (final DeedCallback<Deed<D, P>> iterListener : tempListeners) {

            uiHandler = tempHandlerMap.get(iterListener);

            if (uiHandler != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                iterListener.callback(PodResult.this);
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
    @Override
    public final synchronized void addListener(final DeedCallback<Deed<D, P>> listener) {
        addListener(listener, null);
    }

    /**
     * Method is thread-safe AND blocking
     * Add a PodCallback-listener that will be called when the isFinished() == true, will be called
     * immediately if isFinished() is true when calling addListener()
     */
    @Override
    public final synchronized void addListener(
            final DeedCallback<Deed<D, P>> listener,
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
                                listener.callback(PodResult.this);
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
    @Override
    public final synchronized void removeListener(DeedCallback<Deed<D, P>> listener) {

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
    @Override
    public final D getData() {
        return mData;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the typecasted payload, may be null even if isSuccessful() == true
     */
    @Override
    public final P getPayload() {
        return mPayload;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the exception that caused the failure
     */
    @Override
    public final DeedException getException() {
        return mException;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return the task-id that is generating the result, -1 == not set
     */
    @Override
    public final int getTaskId() {
        return mTaskId;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is finished for consumtion by the caller
     */
    @Override
    public final boolean isFinished() {
        return mFinished;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is successful, always false until isFinished() == true
     */
    @Override
    public final boolean isSuccessful() {
        return mSuccessful;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is successful, returns the same as calling !isSuccessful()
     */
    @Override
    public final boolean isFailed() {
        return !mSuccessful;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return same as calling get() != null
     */
    @Override
    public final boolean hasData() {
        return mData != null;
    }

    /**
     * Method is thread-safe but non-blocking
     * @return same as calling getPayload() != null
     */
    @Override
    public final boolean hasPayload() {
        return mPayload != null;
    }

}
