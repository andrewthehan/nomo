package dev.andrewhan.nomo.examples.breakout.poison

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class PoisonComponent(var duration: Duration) : Component, Exclusive
