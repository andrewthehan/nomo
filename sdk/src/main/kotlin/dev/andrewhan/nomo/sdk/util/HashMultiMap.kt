package dev.andrewhan.nomo.sdk.util

class HashMultiMap<K, V> : MultiMap<K, V>() {
  override fun newMap(): MutableMap<K, MutableSet<V>> = hashMapOf()
  override fun newValueSet(): MutableSet<V> = hashSetOf()
}
