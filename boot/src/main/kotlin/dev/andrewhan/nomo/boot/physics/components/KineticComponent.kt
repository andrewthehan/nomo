package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.math.vectors.MutableVector
import dev.andrewhan.nomo.math.vectors.MutableVector2f
import dev.andrewhan.nomo.math.vectors.MutableVector3f
import dev.andrewhan.nomo.math.vectors.plusAssign
import dev.andrewhan.nomo.math.vectors.zero
import dev.andrewhan.nomo.math.vectors.zeroMutableVector2f
import dev.andrewhan.nomo.math.vectors.zeroMutableVector3f
import dev.andrewhan.nomo.sdk.interfaces.Exclusive

interface KineticComponent<VectorType : MutableVector<Float>> : Component, Exclusive {
  val netForce: VectorType

  fun addForce(force: VectorType) {
    netForce.plusAssign(force)
  }

  fun reset() {
    netForce.zero()
  }
}

class Kinetic2dComponent : KineticComponent<MutableVector2f> {
  override val netForce: MutableVector2f = zeroMutableVector2f()
}

class Kinetic3dComponent : KineticComponent<MutableVector3f> {
  override val netForce: MutableVector3f = zeroMutableVector3f()
}
