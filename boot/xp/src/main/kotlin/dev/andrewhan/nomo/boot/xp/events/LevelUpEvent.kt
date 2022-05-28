package dev.andrewhan.nomo.boot.xp.events

import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event

data class LevelUpEvent(val entity: Entity) : Event
