package dev.andrewhan.nomo.boot.combat.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.interfaces.Exclusive

data class ArmorComponent(val reduction: Float) : Component, Exclusive {
  fun block(damage: Float): Float = damage * (1 - reduction)
}