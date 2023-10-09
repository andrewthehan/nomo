package dev.andrewhan.nomo.sdk.util

import kotlin.reflect.full.createInstance

abstract class MultiMap<K, V> {
  private val map: MutableMap<K, MutableSet<V>> = this.newMap()

  abstract fun newMap(): MutableMap<K, MutableSet<V>>

  abstract fun newValueSet(): MutableSet<V>

  abstract fun <T> Iterable<T>.toCustomSet(): Set<T>

  val entries: Set<Map.Entry<K, Set<V>>>
    get() = map.entries
  val keys: Set<K>
    get() = map.keys
  val values: Collection<Set<V>>
    get() = map.values
  val size: Int
    get() = map.size

  fun clone(): Any {
    val clone = this::class.createInstance()
    entries.forEach { (key, value) -> value.forEach { clone.put(key, it) } }
    return clone
  }

  fun containsKey(key: K): Boolean = map.containsKey(key)

  fun containsValue(value: V): Boolean = map.values.any { it.contains(value) }

  // call toCustomSet() to return a separate instance to avoid concurrent modification issues
  operator fun get(key: K): Set<V> = map[key]?.toCustomSet() ?: newValueSet()

  fun put(key: K, value: V) {
    synchronized(this) {
      val values = map[key] ?: newValueSet()
      values.add(value)
      map[key] = values
    }
  }

  fun remove(key: K): Set<V> = map.remove(key) ?: newValueSet()

  fun remove(key: K, value: V): Boolean {
    synchronized(this) {
      if (!map.containsKey(key)) {
        return false
      }

      val values = map[key]!!
      val removed = values.remove(value)
      if (removed && values.isEmpty()) {
        map.remove(key)
      }
      return removed
    }
  }
}
