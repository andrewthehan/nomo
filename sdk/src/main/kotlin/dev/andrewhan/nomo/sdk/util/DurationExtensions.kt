package dev.andrewhan.nomo.sdk.util

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Duration.toFloat(durationUnits: DurationUnit): Float = toDouble(durationUnits).toFloat()
