package com.ztory.lib.happening.pod;

import com.ztory.lib.happening.typed.TypedHashMap;

/**
 * Created by jonruna on 30/12/15.
 */
public class PodQuery<P> extends TypedHashMap implements PodTyped<P>, PodAsync {

    private boolean mAsync = true;

    public PodQuery() {
        super();
    }

    public PodQuery<P> set(String key, Object val) {
        put(key, val);
        return this;
    }

    @Override
    public boolean isAsync() {
        return mAsync;
    }

    public PodQuery<P> setAsync(boolean theAsync) {
        mAsync = theAsync;
        return this;
    }

}
