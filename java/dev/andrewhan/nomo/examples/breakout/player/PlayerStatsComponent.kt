package dev.andrewhan.nomo.examples.breakout.player

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant

data class PlayerStatsComponent(var segments: Int = 5, var size: Float = 1f) :
        Component, Pendant, Exclusive
