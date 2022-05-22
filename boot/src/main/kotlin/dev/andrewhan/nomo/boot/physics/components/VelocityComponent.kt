package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.math.vectors.MutableVector2f
import dev.andrewhan.nomo.math.vectors.MutableVector3f
import dev.andrewhan.nomo.math.vectors.mutableVectorOf
import dev.andrewhan.nomo.sdk.components.Exclusive

interface VelocityComponent : Component, Exclusive

// delegate's member and interface's hidden member are the same
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class Velocity2dComponent(x: Float = 0f, y: Float = 0f) :
  VelocityComponent, MutableVector2f by mutableVectorOf(x, y) {
  override fun toString() = "${Velocity2dComponent::class.simpleName}(x=$x,y=$y)"
}

// delegate's member and interface's hidden member are the same
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class Velocity3dComponent(x: Float = 0f, y: Float = 0f, z: Float = 0f) :
  VelocityComponent, MutableVector3f by mutableVectorOf(x, y, z) {
  override fun toString() = "${Velocity3dComponent::class.simpleName}(x=$x,y=$y,z=$z)"
}
