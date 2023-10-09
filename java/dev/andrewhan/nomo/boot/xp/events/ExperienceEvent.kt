package dev.andrewhan.nomo.boot.xp.events

import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event

data class ExperienceEvent(val amount: Int, val entity: Entity) : Event
