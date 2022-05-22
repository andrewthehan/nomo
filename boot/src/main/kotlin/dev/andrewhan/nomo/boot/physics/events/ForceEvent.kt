package dev.andrewhan.nomo.boot.physics.events

import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.math.vectors.Vector
import dev.andrewhan.nomo.math.vectors.Vector2f
import dev.andrewhan.nomo.math.vectors.Vector3f

interface ForceEvent<VectorType : Vector<Float>> : Event

data class Force2dEvent(val newtons: Vector2f, val entity: Entity) : ForceEvent<Vector2f>

data class Force3dEvent(val newtons: Vector3f, val entity: Entity) : ForceEvent<Vector3f>
