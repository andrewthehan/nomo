package dev.andrewhan.nomo.sdk.util

class HashBiMultiMap<K, V> : BiMultiMap<K, V> {
  constructor() : super({ reverse -> HashBiMultiMap<V, K>(reverse as HashBiMultiMap<K, V>) })

  private constructor(reverse: HashBiMultiMap<V, K>) : super(reverse)

  override fun <K, V> newMultiMap(): MultiMap<K, V> = HashMultiMap()
}
