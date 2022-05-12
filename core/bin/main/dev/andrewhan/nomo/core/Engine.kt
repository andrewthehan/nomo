package dev.andrewhan.nomo.core

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface Engine {
  @ExperimentalTime suspend fun update(elapsed: Duration)
}
