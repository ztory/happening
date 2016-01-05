package com.ztory.lib.happening.typed;

import android.os.Handler;

import com.ztory.lib.happening.Happening;
import com.ztory.lib.happening.Run;
import com.ztory.lib.happening.result.Deed;
import com.ztory.lib.happening.result.DeedCallback;
import com.ztory.lib.happening.result.DeedException;
import com.ztory.lib.happening.result.DeedSecret;
import com.ztory.lib.happening.result.DeedSetter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * General purpose data object, capable of serving as a query/parameter or data-return.
 * Can also function as a callback-object by setting the key RUN_INTERFACE to a Run instance.
 * If the Run instance added to RUN_INTERFACE returns RUN_RETURN_SLAB then this Slab
 * instance will be returned instead. If no Run instance is found on the RUN_INTERFACE key then
 * this Slab instance will be returned without any additional computation, this can be used to
 * make static data available from everywhere, but released on specific Happening groupIds.
 * Created by jonruna on 01/01/16.
 */
public class Slab<P> extends TypedHashMap implements TypedPayload<P>, Run, DeedSetter<Slab, P> {

    //TODO extenda ConcurrentHashMap instead of HashMap ????

    //TODO WRITE TESTS FOR ALL FUNCTIONALITY IN Slab CLASS !!!!

    public static final Object SLAB_RUN_RETURN = new Object();

    public Slab() {
        super();
    }

    public Slab(int capacity) {
        super(capacity);
    }

    public Slab(P thePayload) {
        super();
        setPayload(thePayload);
    }

    public Slab(int capacity, P thePayload) {
        super(capacity);
        setPayload(thePayload);
    }

    public Slab<P> set(String key, Object val) {
        put(key, val);
        return this;
    }

    public Slab<P> setPayload(P thePayload) {
        return set(PAYLOAD, thePayload);
    }

    @Override
    public void addListener(DeedCallback<Deed<Slab, P>> listener) {
        addListener(listener, null);
    }

    @Override
    public void addListener(final DeedCallback<Deed<Slab, P>> listener, Handler uiHandler) {

        ArrayList<DeedCallback<Deed<Slab, P>>> listeners = typed(LISTENER);
        HashMap<DeedCallback<Deed<Slab, P>>, Handler> handlerMap = typed(HANDLER);

        if (isFinished()) {
            if (uiHandler != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                listener.callback(Slab.this);
                            }
                        }
                );
            }
            else {
                listener.callback(this);
            }
            return;
        }

        if (listeners == null) {
            listeners = new ArrayList<>(1);
            set(LISTENER, listeners);
        }
        listeners.add(listener);

        if (uiHandler != null) {

            if (handlerMap == null) {
                handlerMap = new HashMap<>(1);
                set(HANDLER, handlerMap);
            }

            handlerMap.put(listener, uiHandler);
        }
    }

    @Override
    public void removeListener(DeedCallback<Deed<Slab, P>> listener) {

        ArrayList<DeedCallback<Deed<Slab, P>>> listeners = typed(LISTENER);
        HashMap<DeedCallback<Deed<Slab, P>>, Handler> handlerMap = typed(HANDLER);

        if (listeners != null) {
            listeners.remove(listener);
        }

        if (handlerMap != null) {
            handlerMap.remove(listener);
        }
    }

    @Override
    public Slab getData() {
        return this;
    }

    @Override
    public P getPayload() {
        return typed(PAYLOAD);
    }

    @Override
    public DeedException getException() {
        return typed(EXCEPTION);
    }

    @Override
    public int getTaskId() {
        return typed(TASK);
    }

    @Override
    public boolean isFinished() {
        return typed(FINISHED, false);
    }

    @Override
    public boolean isSuccessful() {
        return typed(SUCCESS, false);
    }

    @Override
    public boolean isFailed() {
        return !isSuccessful();
    }

    @Override
    public boolean hasData() {
        return isFinished() && isSuccessful();
    }

    @Override
    public boolean hasPayload() {
        return isFinished() && isSuccessful() && getPayload() != null;
    }

    @Override
    public void setSuccess(DeedSecret theSecret, Slab theData) throws DeedException {
        setSuccess(theSecret, theData, null);
    }

    @Override
    public void setSuccess(DeedSecret theSecret, Slab theData, P thePayload) throws DeedException {

        DeedSecret secret = typed(SECRET);

        if (secret != null && theSecret.hashCode() != secret.hashCode()) {
            throw new IllegalArgumentException(
                    "theSecret does not match mDataPod.podSecret()."
            );
        }

        set(DATA, theData);

        set(PAYLOAD, thePayload);

        set(SUCCESS, true);

        set(FINISHED, true);

        Run<?, Slab> onSuccessListener = typed(ON_SUCCESS);
        if (onSuccessListener != null) {
            onSuccessListener.r(this);
        }

        notifyDeedListeners();

        Happening.sendEvent(
                getEventGroupId(),
                getEventName(),
                this
        );
    }

    @Override
    public void setFailed(DeedSecret theSecret, DeedException theException) {

        DeedSecret secret = typed(SECRET);

        if (secret != null && theSecret.hashCode() != secret.hashCode()) {
            throw new IllegalArgumentException(
                    "theSecret does not match mDataPod.podSecret()."
            );
        }

        set(EXCEPTION, theException);

        set(SUCCESS, false);

        set(FINISHED, true);

        Run<?, Slab> onFailListener = typed(ON_FAIL);
        if (onFailListener != null) {
            onFailListener.r(this);
        }

        notifyDeedListeners();

        Happening.sendEvent(
                getEventGroupId(),
                getEventName(),
                this
        );
    }

    public int getEventGroupId() {
        return typed(GROUP, Happening.GROUP_ID_GLOBAL);
    }

    public String getEventName() {
        return typed(EVENT, Happening.getEventName(getClass(), "broadcast"));
    }

    public void notifyDeedListeners() {

        ArrayList<DeedCallback<Deed<Slab, P>>> tempListeners = typed(LISTENER);
        HashMap<DeedCallback<Deed<Slab, P>>, Handler> tempHandlerMap;

        if (tempListeners == null) {
            return;
        }

        tempHandlerMap = typed(HANDLER);

        Handler uiHandler = null;

        for (final DeedCallback<Deed<Slab, P>> iterListener : tempListeners) {

            if (tempHandlerMap != null) {
                uiHandler = tempHandlerMap.get(iterListener);
            }

            if (uiHandler != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                iterListener.callback(Slab.this);
                            }
                        }
                );
            }
            else {
                iterListener.callback(this);
            }
        }
    }

    @Override
    public Object r(Object o) {

        Run callbackRun = typed(RUN_INTERFACE);

        if (callbackRun != null) {
            try {

                Object runReturn = callbackRun.r(o);

                if (SLAB_RUN_RETURN.equals(runReturn)) {
                    return this;
                }
                else {
                    return runReturn;
                }

            } catch (Exception e) {
                return e;
            }
        }

        return this;
    }

}
