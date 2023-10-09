package dev.andrewhan.nomo.sdk.util

import java.lang.reflect.Type

fun Class<*>.getAllAssignableTypes(): Iterable<Type> {
  val types = mutableSetOf<Type>()
  var c: Class<*>? = this
  while (c != null) {
    types.addAll(c.genericInterfaces)
    c.genericSuperclass?.let {
      types.add(it)
    }
    c = c.superclass
  }
  return types
}
