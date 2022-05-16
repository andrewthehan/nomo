package dev.andrewhan.nomo.boot.combat.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.interfaces.Exclusive
import kotlin.math.max
import kotlin.math.min

data class HealthComponent(var health: Float, val maxHealth: Float = Float.MAX_VALUE) :
  Component, Exclusive {
  val isAlive: Boolean get() = health > 0
  val isDead: Boolean get() = health == 0f

  fun damage(amount: Float) {
    require(amount >= 0.0) { "Damage amount should be non-negative: $amount" }

    health = max(health - amount, 0f)
  }

  fun heal(amount: Float) {
    require(amount >= 0.0) { "Heal amount should be non-negative: $amount" }

    health = min(health + amount, maxHealth)
  }
}