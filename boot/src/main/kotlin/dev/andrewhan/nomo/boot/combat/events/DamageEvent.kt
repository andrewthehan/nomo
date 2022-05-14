package dev.andrewhan.nomo.boot.combat.events

import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event

data class DamageEvent(var amount: Float, val entity: Entity) : Event {
  val hasDamage: Boolean
    get() = amount > 0.0
}
