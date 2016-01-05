package com.ztory.lib.happening.typed;

import java.util.Map;

/**
 * Created by jonruna on 31/12/15.
 */
public interface TypedMap<K, V> extends Map<K, V> {

    /** General keys to be used by TypedMap instances. */
    String
            TYPEDMAP_NAME = TypedMap.class.getName() + ".",
            PROTOTYPE = TYPEDMAP_NAME + "prototype",
            UP = TYPEDMAP_NAME + "up",
            DOWN = TYPEDMAP_NAME + "down",
            NORTH = TYPEDMAP_NAME + "north",
            EAST = TYPEDMAP_NAME + "east",
            SOUTH = TYPEDMAP_NAME + "south",
            WEST = TYPEDMAP_NAME + "west",
            PARENT = TYPEDMAP_NAME + "parent",
            CHILD = TYPEDMAP_NAME + "child",
            ID = TYPEDMAP_NAME + "id",
            X = TYPEDMAP_NAME + "x",
            Y = TYPEDMAP_NAME + "y",
            Z = TYPEDMAP_NAME + "z",
            WIDTH = TYPEDMAP_NAME + "width",
            HEIGHT = TYPEDMAP_NAME + "height",
            DEPTH = TYPEDMAP_NAME + "depth",
            IMAGE = TYPEDMAP_NAME + "image",
            BITMAP = TYPEDMAP_NAME + "bitmap",
            UI = TYPEDMAP_NAME + "ui",
            SCREEN = TYPEDMAP_NAME + "screen",
            LAYOUT = TYPEDMAP_NAME + "layout",
            VIEW = TYPEDMAP_NAME + "view",
            LOADED = TYPEDMAP_NAME + "loaded",
            SIZE = TYPEDMAP_NAME + "size",
            BOUNDS = TYPEDMAP_NAME + "bounds",
            PAINT = TYPEDMAP_NAME + "paint",
            STYLE = TYPEDMAP_NAME + "style",
            HANDLER = TYPEDMAP_NAME + "handler",
            POST = TYPEDMAP_NAME + "post",
            REFERENCE = TYPEDMAP_NAME + "reference",
            WAITING = TYPEDMAP_NAME + "waiting",
            TASK = TYPEDMAP_NAME + "task",
            ASYNC = TYPEDMAP_NAME + "async",
            URI = TYPEDMAP_NAME + "uri",
            PATH = TYPEDMAP_NAME + "path",
            VERB = TYPEDMAP_NAME + "verb",
            HEADERS = TYPEDMAP_NAME + "headers",
            QUERY = TYPEDMAP_NAME + "query",
            JSON = TYPEDMAP_NAME + "json",
            GSON = TYPEDMAP_NAME + "gson",
            RUN = TYPEDMAP_NAME + "run",
            BYTES = TYPEDMAP_NAME + "bytes",
            NAME = TYPEDMAP_NAME + "name",
            MODE = TYPEDMAP_NAME + "mode",
            TYPE = TYPEDMAP_NAME + "type",
            CLASS = TYPEDMAP_NAME + "class",
            INTERFACE = TYPEDMAP_NAME + "interface",
            EXTRA = TYPEDMAP_NAME + "extra",
            DATA = TYPEDMAP_NAME + "data",
            PAYLOAD = TYPEDMAP_NAME + "payload",
            EVENT = TYPEDMAP_NAME + "event",
            GROUP = TYPEDMAP_NAME + "group",
            LISTENER = TYPEDMAP_NAME + "listener",
            CALLBACK = TYPEDMAP_NAME + "callback",
            HAPPENING = TYPEDMAP_NAME + "happening",
            HAPPENING_LISTENER = TYPEDMAP_NAME + "happening_listener",
            RUN_INTERFACE = TYPEDMAP_NAME + "run_interface",
            PARAM = TYPEDMAP_NAME + "param",
            RETURN = TYPEDMAP_NAME + "return",
            TEMP = TYPEDMAP_NAME + "temp",
            EXCEPTION = TYPEDMAP_NAME + "exception",
            ERROR = TYPEDMAP_NAME + "error",
            FAIL = TYPEDMAP_NAME + "fail",
            SUCCESS = TYPEDMAP_NAME + "success",
            FINISHED = TYPEDMAP_NAME + "finished",
            REQUEST = TYPEDMAP_NAME + "request",
            RESPONSE = TYPEDMAP_NAME + "response",
            RESULT = TYPEDMAP_NAME + "result",
            TYPED = TYPEDMAP_NAME + "typed",
            PARAMETERIZED = TYPEDMAP_NAME + "parameterized",
            PARSE = TYPEDMAP_NAME + "parse",
            ENCRYPTED = TYPEDMAP_NAME + "encrypted",
            SECRET = TYPEDMAP_NAME + "secret";

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
