package com.ztory.lib.happening.typed;

import java.util.Map;

/**
 * Created by jonruna on 31/12/15.
 */
public interface TypedMap<K, V> extends Map<K, V> {

    /** General keys to be used by TypedMap instances. */
    String
            PROTOTYPE = "prototype",
            PARENT = "parent",
            CHILD = "child",
            ID = "id",
            X = "x",
            Y = "y",
            Z = "z",
            WIDTH = "width",
            HEIGHT = "height",
            DEPTH = "depth",
            BITMAP = "bitmap",
            UI = "ui",
            LOADED = "loaded",
            SIZE = "size",
            BOUNDS = "bounds",
            PAINT = "paint",
            STYLE = "style",
            HANDLER = "handler",
            POST = "post",
            REFERENCE = "reference",
            TASK = "task",
            ASYNC = "async",
            URI = "uri",
            PATH = "path",
            VERB = "verb",
            HEADERS = "headers",
            QUERY = "query",
            RESULT = "result",
            JSON = "json",
            GSON = "gson",
            RUN = "run",
            BYTES = "bytes",
            NAME = "name",
            MODE = "mode",
            TYPE = "type",
            EXTRA = "extra",
            DATA = "data",
            PAYLOAD = "payload",
            EVENT = "event",
            GROUP = "group",
            LISTENER = "listener",
            CALLBACK = "callback",
            PARAM = "param",
            RETURN = "return",
            TEMP = "temp",
            EXCEPTION = "exception",
            ERROR = "error",
            FAIL = "fail",
            SUCCESS = "success",
            ON_FAIL = "on_fail",
            ON_SUCCESS = "on_success",
            STATUS = "status",
            STATE = "state",
            FINISHED = "finished",
            REQUEST = "request",
            RESPONSE = "response",
            PARAMETERIZED = "parameterized",
            PARSE = "parse",
            ENCRYPTED = "encrypted",
            SECRET = "secret";

    /**
     * Default implementing code for this method is:
     * <code>return Typed.get(get(key))</code>
     * @param key key of the value that you want to get from the Map
     * @param <T> type that you want the value to be cast into
     * @return with the default implementation then the value is returned as T. Will return
     * null if the value can not be cast into a T-instance, or if get(key) returned null.
     */
    <T> T typed(String key);

    /**
     * Default implementing code for this method is:
     * <code>return Typed.get(get(key), defaultValue)</code>
     * @param key key of the value that you want to get from the Map
     * @param defaultValue the value that will be returned instead of <code>null</code>
     * @param <T> type that you want the value to be cast into
     * @return with the default implementation then the value is returned as T. Will return
     * defaultValue if the value can not be cast into a T-instance, or if get(key) returned null.
     */
    <T> T typed(String key, T defaultValue);
}
