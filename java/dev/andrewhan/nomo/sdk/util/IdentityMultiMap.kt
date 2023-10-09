package dev.andrewhan.nomo.sdk.util

import java.util.Collections
import java.util.IdentityHashMap

class IdentityMultiMap<K, V> : MultiMap<K, V>() {
  override fun newMap(): MutableMap<K, MutableSet<V>> =
    Collections.synchronizedMap(IdentityHashMap())
  override fun newValueSet(): MutableSet<V> = emptyMutableIdentitySet()
  override fun <T> Iterable<T>.toCustomSet(): Set<T> = toIdentitySet()
}
