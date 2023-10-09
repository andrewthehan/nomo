package dev.andrewhan.nomo.sdk.util

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Duration.toFloat(durationUnits: DurationUnit): Float = toDouble(durationUnits).toFloat()

@ExperimentalTime
val Duration.isZero: Boolean
  get() = this == Duration.ZERO

@ExperimentalTime
fun min(durationA: Duration, durationB: Duration): Duration =
  if (durationA <= durationB) {
    durationA
  } else {
    durationB
  }

@ExperimentalTime
fun max(durationA: Duration, durationB: Duration): Duration =
  if (durationA >= durationB) {
    durationA
  } else {
    durationB
  }
