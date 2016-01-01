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
            ASYNC = TYPEDMAP_NAME + "async",
            ID = TYPEDMAP_NAME + "id",
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
