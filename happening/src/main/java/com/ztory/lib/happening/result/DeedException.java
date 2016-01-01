package com.ztory.lib.happening.result;

/**
 * Exception class used by Deed instances
 * Created by jonruna on 26/08/15.
 */
public class DeedException extends Exception {

    private int mCode = -1;

    public DeedException(Throwable throwable) {
        super(throwable);
    }

    public DeedException(String detailMessage) {
        super(detailMessage);
    }

    public DeedException(String detailMessage, int theCode) {
        super(detailMessage);

        setCode(theCode);
    }

    public DeedException(String detailMessage, int theCode, Throwable throwable) {
        super(detailMessage, throwable);

        setCode(theCode);
    }

    public int getCode() {
        return mCode;
    }

    public DeedException setCode(int theCode) {
        mCode = theCode;
        return this;
    }
}
