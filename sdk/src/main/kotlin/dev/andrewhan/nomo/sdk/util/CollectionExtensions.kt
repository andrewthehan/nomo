package dev.andrewhan.nomo.sdk.util

import java.util.Collections
import java.util.IdentityHashMap

fun <T> emptyIdentitySet(): Set<T> =
  Collections.synchronizedSet(Collections.newSetFromMap(IdentityHashMap()))

fun <T> emptyMutableIdentitySet(): MutableSet<T> =
  Collections.synchronizedSet(Collections.newSetFromMap(IdentityHashMap()))

fun <T> Iterable<T>.toIdentitySet(): Set<T> =
  toCollection(Collections.synchronizedSet(Collections.newSetFromMap(IdentityHashMap())))
