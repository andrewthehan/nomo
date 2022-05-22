package dev.andrewhan.nomo.sdk.lifecycle

interface Lifecycle {
  suspend fun start() = Unit
  suspend fun stop() = Unit
}