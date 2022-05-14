package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.math.vectors.MutableVector2f
import dev.andrewhan.nomo.math.vectors.MutableVector3f
import dev.andrewhan.nomo.math.vectors.mutableVectorOf
import dev.andrewhan.nomo.sdk.interfaces.Exclusive

interface PositionComponent : Component, Exclusive

// delegate's member and interface's hidden member are the same
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class Position2dComponent(x: Float = 0F, y: Float = 0F) :
  PositionComponent, MutableVector2f by mutableVectorOf(x, y) {
  override fun toString() = "${Position2dComponent::class.simpleName}(x=$x,y=$y)"
}

// delegate's member and interface's hidden member are the same
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class Position3dComponent(x: Float = 0F, y: Float = 0F, z: Float = 0F) :
  PositionComponent, MutableVector3f by mutableVectorOf(x, y, z) {
  override fun toString() = "${Position3dComponent::class.simpleName}(x=$x,y=$y,z=$z)"
}
