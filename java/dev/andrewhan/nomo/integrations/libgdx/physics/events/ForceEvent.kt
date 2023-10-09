package dev.andrewhan.nomo.integration.libgdx.physics.events

import com.badlogic.gdx.math.Vector2
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event

data class ForceEvent(val newtons: Vector2, val entity: Entity) : Event
