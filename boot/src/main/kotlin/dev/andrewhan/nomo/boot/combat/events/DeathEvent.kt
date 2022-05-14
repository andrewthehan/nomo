package dev.andrewhan.nomo.boot.combat.events

import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event

data class DeathEvent(val entity: Entity) : Event