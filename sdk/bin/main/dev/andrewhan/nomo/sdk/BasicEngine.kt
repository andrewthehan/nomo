package dev.andrewhan.nomo.sdk

import dev.andrewhan.nomo.core.Engine
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class BasicEngine : Engine {
  @ExperimentalTime
  override suspend fun update(elapsed: Duration) {
    println(elapsed)
  }
}
