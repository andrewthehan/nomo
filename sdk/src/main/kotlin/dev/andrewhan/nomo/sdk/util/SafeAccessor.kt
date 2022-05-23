package dev.andrewhan.nomo.sdk.util

open class SafeAccessor<T : Any>(private val value: T) {
  fun <R> safeRun(runner: (T) -> R): R = synchronized(value) { value.let(runner) }
}
