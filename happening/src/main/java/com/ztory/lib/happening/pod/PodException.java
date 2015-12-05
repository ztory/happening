package com.ztory.lib.happening.pod;

/**
 * Created by jonruna on 26/08/15.
 */
public class PodException extends Exception {

    private int mCode = -1;

    public PodException(Throwable throwable) {
        super(throwable);
    }

    public PodException(String detailMessage) {
        super(detailMessage);
    }

    public PodException(String detailMessage, int theCode) {
        super(detailMessage);

        setCode(theCode);
    }

    public PodException(String detailMessage, int theCode, Throwable throwable) {
        super(detailMessage, throwable);

        setCode(theCode);
    }

    public int getCode() {
        return mCode;
    }

    public PodException setCode(int theCode) {
        mCode = theCode;
        return this;
    }
}
