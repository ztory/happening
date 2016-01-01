package com.ztory.lib.happening.result;

import android.os.Handler;

/**
 * Interface representing an arbitrary Deed. When a deed is finished it is either successful or
 * failed. When it is failed it will have a DeedException describing the cause for the failure.
 * When it is successful the consumer can call getData() and getPayload() either or both MAY be
 * null.
 * Created by jonruna on 30/12/15.
 */
public interface Deed<D, P> {

    /**
     * Method is thread-safe AND blocking
     * Add a PodCallback-listener that will be called when the isFinished() == true, will be called
     * immediately if isFinished() is true when calling addListener()
     */
    void addListener(final DeedCallback<Deed<D, P>> listener);

    /**
     * Method is thread-safe AND blocking
     * Add a PodCallback-listener that will be called when the isFinished() == true, will be called
     * immediately if isFinished() is true when calling addListener()
     */
    void addListener(
            final DeedCallback<Deed<D, P>> listener,
            final Handler uiHandler
    );

    /**
     * Method is thread-safe AND blocking
     * Remove a previously added PodCallback-listener
     */
    void removeListener(DeedCallback<Deed<D, P>> listener);

    /**
     * Method is thread-safe but non-blocking
     * @return the typecasted data, may be null even if isSuccessful() == true
     */
    D getData();

    /**
     * Method is thread-safe but non-blocking
     * @return the typecasted payload, may be null even if isSuccessful() == true
     */
    P getPayload();

    /**
     * Method is thread-safe but non-blocking
     * @return the exception that caused the failure
     */
    DeedException getException();

    /**
     * Method is thread-safe but non-blocking
     * @return the task-id that is generating the result, -1 == not set
     */
    int getTaskId();

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is finished for consumtion by the caller
     */
    boolean isFinished();

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is successful, always false until isFinished() == true
     */
    boolean isSuccessful();

    /**
     * Method is thread-safe but non-blocking
     * @return if the result is successful, returns the same as calling !isSuccessful()
     */
    boolean isFailed();

    /**
     * Method is thread-safe but non-blocking
     * @return same as calling get() != null
     */
    boolean hasData();

    /**
     * Method is thread-safe but non-blocking
     * @return same as calling getPayload() != null
     */
    boolean hasPayload();

}
