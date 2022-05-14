package dev.andrewhan.nomo.boot.collision.events

import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event

data class CollisionEvent(val entityA: Entity, val entityB: Entity) : Event {}
