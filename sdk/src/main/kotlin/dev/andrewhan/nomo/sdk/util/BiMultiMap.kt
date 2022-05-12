package dev.andrewhan.nomo.sdk.util

class BiMultiMap<K, V> {
  private val forwardMap: MultiMap<K, V>
  private val reverseMap: MultiMap<V, K>
  private val reverse: BiMultiMap<V, K>

  constructor() {
    forwardMap = MultiMap()
    reverseMap = MultiMap()
    reverse = BiMultiMap(reverseMap, forwardMap, this)
  }

  private constructor(
    forwardMap: MultiMap<K, V>,
    reverseMap: MultiMap<V, K>,
    reverse: BiMultiMap<V, K>
  ) {
    this.forwardMap = forwardMap
    this.reverseMap = reverseMap
    this.reverse = reverse
  }

  fun clone(): BiMultiMap<K, V> {
    return BiMultiMap<K, V>().apply {
      forwardMap.entries.forEach { (key, value) -> value.forEach { this.put(key, it) } }
    }
  }

  fun getKeys() = forwardMap.keys.toSet()

  fun getValues() = reverseMap.keys.toSet()

  operator fun get(key: K) = forwardMap[key].toSet()

  fun getByValue(value: V) = reverseMap[value].toSet()

  fun containsKey(key: K) = forwardMap.containsKey(key)

  fun containsValue(value: V) = reverseMap.containsKey(value)

  fun put(key: K, value: V) {
    forwardMap.put(key, value)
    reverseMap.put(value, key)
  }

  fun removeKey(key: K): MutableSet<V>? {
    val values = forwardMap.remove(key)
    values?.forEach { reverseMap.remove(it, key) }
    return values
  }

  fun removeValue(value: V): MutableSet<K>? {
    val keys = reverseMap.remove(value)
    keys?.forEach { forwardMap.remove(it, value) }
    return keys
  }

  fun remove(key: K, value: V) = forwardMap.remove(key, value) && reverseMap.remove(value, key)
}
