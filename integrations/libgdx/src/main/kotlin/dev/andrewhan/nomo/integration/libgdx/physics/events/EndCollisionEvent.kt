package dev.andrewhan.nomo.integration.libgdx.physics.events

import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event

data class EndCollisionEvent(val world: World, val entityA: Entity, val entityB: Entity) : Event
