package com.ztory.lib.happening.typed;

import java.util.Map;

/**
 * Created by jonruna on 31/12/15.
 */
public interface TypedMap<K, V> extends Map<K, V> {
    <T> T getTyped(String key);
    <T> T getTyped(String key, T defaultValue);
}
