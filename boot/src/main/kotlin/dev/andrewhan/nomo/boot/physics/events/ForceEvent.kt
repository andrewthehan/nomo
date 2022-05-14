package dev.andrewhan.nomo.boot.physics.events

import dev.andrewhan.nomo.math.vectors.Vector
import dev.andrewhan.nomo.math.vectors.Vector2f
import dev.andrewhan.nomo.math.vectors.Vector3f

interface ForceEvent<VectorType : Vector<Float>>

data class Force2dEvent(val newtons: Vector2f) : ForceEvent<Vector2f>

data class Force3dEvent(val newtons: Vector3f) : ForceEvent<Vector3f>
