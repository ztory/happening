package com.ztory.lib.happening.typed;

/**
 * Created by jonruna on 31/12/15.
 */
public class StTyped {

    public static <T> T getTyped(Object value) {

        if (value == null) {
            return null;
        }

        try {
            return (T) value;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static <T> T getTyped(Object value, T defaultValue) {
        T returnVal = getTyped(value);
        return (returnVal != null) ? returnVal : defaultValue;
    }

}
