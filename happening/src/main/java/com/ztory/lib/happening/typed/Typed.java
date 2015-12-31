package com.ztory.lib.happening.typed;

/**
 * Created by jonruna on 31/12/15.
 */
public class Typed {

    public static <T> T get(Object value) {

        if (value == null) {
            return null;
        }

        try {
            return (T) value;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static <T> T get(Object value, T defaultValue) {
        T returnVal = get(value);
        return (returnVal != null) ? returnVal : defaultValue;
    }

}
