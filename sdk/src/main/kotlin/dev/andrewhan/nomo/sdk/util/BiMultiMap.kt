package dev.andrewhan.nomo.sdk.util

import kotlin.reflect.full.createInstance

abstract class BiMultiMap<K, V> {
  // var instead of val to support the constructor
  private var forwardMap: MultiMap<K, V>
  private var reverseMap: MultiMap<V, K>
  private var reverse: BiMultiMap<V, K>

  abstract fun <K, V> newMultiMap(): MultiMap<K, V>

  abstract fun <T> Iterable<T>.toCustomSet(): Set<T>

  private fun self() = this

  constructor(reverse: BiMultiMap<V, K>) {
    reverse.also {
      this.forwardMap = it.reverseMap
      this.reverseMap = it.forwardMap
      this.reverse = it
    }
  }

  constructor(reverseConstructor: (forward: BiMultiMap<K, V>) -> BiMultiMap<V, K>) {
    forwardMap = this.newMultiMap()
    reverseMap = this.newMultiMap()
    reverse =
      reverseConstructor(self()).also {
        it.forwardMap = reverseMap
        it.reverseMap = forwardMap
        it.reverse = self()
      }
  }

  fun clone(): BiMultiMap<K, V> {
    return this::class.createInstance().apply {
      forwardMap.entries.forEach { (key, value) -> value.forEach { this.put(key, it) } }
    }
  }

  fun getKeys(): Set<K> = synchronized(forwardMap) { forwardMap.keys.toCustomSet() }

  fun getValues(): Set<V> = synchronized(reverseMap) { reverseMap.keys.toCustomSet() }

  operator fun get(key: K): Set<V> = synchronized(forwardMap) { forwardMap[key].toCustomSet() }

  fun getByValue(value: V): Set<K> = synchronized(reverseMap) { reverseMap[value].toCustomSet() }

  fun containsKey(key: K): Boolean = forwardMap.containsKey(key)

  fun containsValue(value: V): Boolean = reverseMap.containsKey(value)

  fun put(key: K, value: V) {
    synchronized(this) {
      forwardMap.put(key, value)
      reverseMap.put(value, key)
    }
  }

  fun removeKey(key: K): Set<V> {
    synchronized(this) {
      val values = forwardMap.remove(key)
      assert(values.all { reverseMap.remove(it, key) }) {
        "All values removed from the forwardMap should exist in the reverseMap but do not."
      }
      return values
    }
  }

  fun removeValue(value: V): Set<K> {
    synchronized(this) {
      val keys = reverseMap.remove(value)
      assert(keys.all { forwardMap.remove(it, value) }) {
        "All keys removed from the reverseMap should exist in the forwardMap but do not."
      }
      return keys
    }
  }

  fun remove(key: K, value: V): Boolean =
    synchronized(this) { forwardMap.remove(key, value) && reverseMap.remove(value, key) }
}
