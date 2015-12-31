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
            KEY_NAME = KEY_PREFIX + "name",
            KEY_TYPE = KEY_PREFIX + "type",
            KEY_EXTRA = KEY_PREFIX + "extra",
            KEY_DATA = KEY_PREFIX + "data",
            KEY_PAYLOAD = KEY_PREFIX + "payload";

    <T> T getTyped(String key);
    <T> T getTyped(String key, T defaultValue);
}
