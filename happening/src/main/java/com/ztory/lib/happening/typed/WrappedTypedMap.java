package com.ztory.lib.happening.typed;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by jonruna on 31/12/15.
 */
public class WrappedTypedMap<K, V, M extends Map<K, V>> implements TypedMap<K, V> {

    private final M mWrappedMap;

    public WrappedTypedMap(M theWrappedMap) {
        mWrappedMap = theWrappedMap;
    }

    public M getWrappedMap() {
        return mWrappedMap;
    }

    @Override
    public <T> T getTyped(String key) {
        return Typed.get(get(key));
    }

    @Override
    public <T> T getTyped(String key, T defaultValue) {
        return Typed.get(get(key), defaultValue);
    }

    @Override
    public void clear() {
        mWrappedMap.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return mWrappedMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return mWrappedMap.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return mWrappedMap.entrySet();
    }

    @Override
    public V get(Object key) {
        return mWrappedMap.get(key);
    }

    @Override
    public boolean isEmpty() {
        return mWrappedMap.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return mWrappedMap.keySet();
    }

    @Override
    public V put(K key, V value) {
        return mWrappedMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        mWrappedMap.putAll(map);
    }

    @Override
    public V remove(Object key) {
        return mWrappedMap.remove(key);
    }

    @Override
    public int size() {
        return mWrappedMap.size();
    }

    @Override
    public Collection<V> values() {
        return mWrappedMap.values();
    }
}
