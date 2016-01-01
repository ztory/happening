package com.ztory.lib.happening.typed;

import java.util.Map;

/**
 * Created by jonruna on 31/12/15.
 */
public interface TypedMap<K, V> extends Map<K, V> {

    /** General keys to be used by TypedMap instances. */
    String
            KEY_PREFIX = TypedMap.class.getName() + ".",
            KEY_PROTOTYPE = KEY_PREFIX + "prototype",
            KEY_UP = KEY_PREFIX + "up",
            KEY_DOWN = KEY_PREFIX + "down",
            KEY_NORTH = KEY_PREFIX + "north",
            KEY_EAST = KEY_PREFIX + "east",
            KEY_SOUTH = KEY_PREFIX + "south",
            KEY_WEST = KEY_PREFIX + "west",
            KEY_PARENT = KEY_PREFIX + "parent",
            KEY_CHILD = KEY_PREFIX + "child",
            KEY_ASYNC = KEY_PREFIX + "async",
            KEY_ID = KEY_PREFIX + "id",
            KEY_URI = KEY_PREFIX + "uri",
            KEY_PATH = KEY_PREFIX + "path",
            KEY_VERB = KEY_PREFIX + "verb",
            KEY_HEADERS = KEY_PREFIX + "headers",
            KEY_QUERY = KEY_PREFIX + "query",
            KEY_JSON = KEY_PREFIX + "json",
            KEY_GSON = KEY_PREFIX + "gson",
            KEY_RUN = KEY_PREFIX + "run",
            KEY_BYTES = KEY_PREFIX + "bytes",
            KEY_NAME = KEY_PREFIX + "name",
            KEY_MODE = KEY_PREFIX + "mode",
            KEY_TYPE = KEY_PREFIX + "type",
            KEY_CLASS = KEY_PREFIX + "class",
            KEY_EXTRA = KEY_PREFIX + "extra",
            KEY_DATA = KEY_PREFIX + "data",
            KEY_PAYLOAD = KEY_PREFIX + "payload",
            KEY_PARSE = KEY_PREFIX + "parse",
            KEY_ENCRYPTED = KEY_PREFIX + "encrypted",
            KEY_SECRET = KEY_PREFIX + "secret";

    /**
     * Default implementing code for this method is:
     * <code>return Typed.get(get(key))</code>
     * @param key key of the value that you want to get from the Map
     * @param <T> type that you want the value to be cast into
     * @return with the default implementation then the value is returned as T. Will return
     * null if the value can not be cast into a T-instance, or if get(key) returned null.
     */
    <T> T getTyped(String key);

    /**
     * Default implementing code for this method is:
     * <code>return Typed.get(get(key), defaultValue)</code>
     * @param key key of the value that you want to get from the Map
     * @param defaultValue the value that will be returned instead of <code>null</code>
     * @param <T> type that you want the value to be cast into
     * @return with the default implementation then the value is returned as T. Will return
     * defaultValue if the value can not be cast into a T-instance, or if get(key) returned null.
     */
    <T> T getTyped(String key, T defaultValue);
}
