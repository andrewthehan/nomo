package dev.andrewhan.nomo.sdk.util

class IdentityBiMultiMap<K, V> : BiMultiMap<K, V> {
  constructor() :
    super({ reverse -> IdentityBiMultiMap<V, K>(reverse as IdentityBiMultiMap<K, V>) })

  private constructor(reverse: IdentityBiMultiMap<V, K>) : super(reverse)

  override fun <K, V> newMultiMap(): MultiMap<K, V> = IdentityMultiMap()

  override fun <T> Iterable<T>.toCustomSet(): Set<T> = toIdentitySet()
}
