package dev.andrewhan.nomo.example.player

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant

data class PlayerStatsComponent(var segments: Int = 10, var size: Float = 1f) :
  Component, Pendant, Exclusive
