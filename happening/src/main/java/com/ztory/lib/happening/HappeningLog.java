package com.ztory.lib.happening;

import android.util.Log;

/**
 * Simple log helper class that prints Log.d() statements in a key-value pattern.
 * Created by jonruna on 01/12/15.
 */
public class HappeningLog {

    /**
     * Defaults to false, so will not log anything if not explicitly enabled.
     * Call <code>HappeningLog.LOG_ENABLED = BuildConfig.DEBUG;</code> when starting your
     * application to enable log only when debugging.
     */
    public static boolean LOG_ENABLED = false;

    private static final String
            LOG_TAG_PRE = "[",
            LOG_TAG_POST = "] ",
            KV_SEPARATOR = " | ",
            VALUE_PREFIX = ": ";

    public static void log(Class callingClass, Object... msgs) {

        if (!LOG_ENABLED) {
            return;
        }

        if (msgs.length == 0) {
            throw new IllegalArgumentException("msgs.length must be greater than zero.");
        }
        else if (msgs.length % 2 != 0) {
            throw new IllegalArgumentException("msgs.length must be dividable by 2.");
        }

        String logTag = callingClass.getSimpleName();

        StringBuilder sb = new StringBuilder();

        sb.append(LOG_TAG_PRE);
        sb.append(logTag);
        sb.append(LOG_TAG_POST);

        for (int i = 0; i < msgs.length; i += 2) {

            if (i > 0) {
                sb.append(KV_SEPARATOR);
            }
            sb.append(msgs[i]);
            sb.append(VALUE_PREFIX);
            sb.append(msgs[i + 1]);
        }

        String entireLog = sb.toString();

        Log.d(logTag, entireLog);
    }

}
