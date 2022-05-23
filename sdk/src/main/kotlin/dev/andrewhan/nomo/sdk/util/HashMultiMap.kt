package dev.andrewhan.nomo.sdk.util

import java.util.concurrent.ConcurrentHashMap

class HashMultiMap<K, V> : MultiMap<K, V>() {
  override fun newMap(): MutableMap<K, MutableSet<V>> = ConcurrentHashMap()
  override fun newValueSet(): MutableSet<V> = ConcurrentHashMap.newKeySet()
  override fun <T> Iterable<T>.toCustomSet(): Set<T> = toCollection(ConcurrentHashMap.newKeySet())
}
