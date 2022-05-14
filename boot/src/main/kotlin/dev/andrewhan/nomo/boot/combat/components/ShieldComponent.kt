package dev.andrewhan.nomo.boot.combat.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.interfaces.Exclusive
import kotlin.math.min

data class ShieldComponent(var amount: Float) : Component, Exclusive {
  val isDepleted
    get() = amount <= 0

  fun absorb(damage: Float): Float {
    val damageMitigated = min(damage, amount)

    amount -= damageMitigated

    return damage - damageMitigated
  }
}