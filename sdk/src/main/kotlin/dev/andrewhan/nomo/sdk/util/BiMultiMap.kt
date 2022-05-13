package dev.andrewhan.nomo.sdk.util

import kotlin.reflect.full.createInstance

abstract class BiMultiMap<K, V> {
  // var instead of val to support the constructor
  private var forwardMap: MultiMap<K, V>
  private var reverseMap: MultiMap<V, K>
  private var reverse: BiMultiMap<V, K>

  abstract fun <K, V> newMultiMap(): MultiMap<K, V>

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

  fun getKeys(): Set<K> = forwardMap.keys.toSet()

  fun getValues(): Set<V> = reverseMap.keys.toSet()

  operator fun get(key: K): Set<V> = forwardMap[key].toSet()

  fun getByValue(value: V): Set<K> = reverseMap[value].toSet()

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
      if (!values.all { reverseMap.remove(it, key) }) {
        throw AssertionError(
          "All values removed from the forwardMap are expected to exist in the " +
            "reverseMap but were not."
        )
      }
      return values
    }
  }

  fun removeValue(value: V): Set<K> {
    synchronized(this) {
      val keys = reverseMap.remove(value)
      if (!keys.all { forwardMap.remove(it, value) }) {
        throw AssertionError(
          "All keys removed from the reverseMap are expected to exist in the " +
            "forwardMap but were not."
        )
      }
      return keys
    }
  }

  fun remove(key: K, value: V): Boolean =
    synchronized(this) { forwardMap.remove(key, value) && reverseMap.remove(value, key) }
}
