package dev.andrewhan.nomo.sdk.events

import dev.andrewhan.nomo.core.Event
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class UpdateEvent @OptIn(ExperimentalTime::class) constructor(val elapsed: Duration) : Event
