package com.ztory.lib.happening.pod;

import android.os.Handler;

/**
 * Created by jonruna on 29/12/15.
 */
public interface IRes<D, P> {

    void setSuccess(PodSecret theSecret, D theData) throws PodException;

    void setSuccess(PodSecret theSecret, D theData, P thePayload) throws PodException;

    void setFailed(PodSecret theSecret, PodException theException);

    /**
     * Method is thread-safe AND blocking
     * Add a PodCallback-listener that will be called when the isFinished() == true, will be called
     * immediately if isFinished() is true when calling addListener()
     */
    void addListener(final PodCallback<IRes<D, P>> listener);

    /**
     * Method is thread-safe AND blocking
     * Add a PodCallback-listener that will be called when the isFinished() == true, will be called
     * immediately if isFinished() is true when calling addListener()
     */
    void addListener(
            final PodCallback<IRes<D, P>> listener,
            final Handler uiHandler
    );

    /**
     * Method is thread-safe AND blocking
     * Remove a previously added PodCallback-listener
     */
    void removeListener(PodCallback<IRes<D, P>> listener);

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
    PodException getException();

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
