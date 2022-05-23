package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.math.vectors.Vector
import dev.andrewhan.nomo.math.vectors.Vector2f
import dev.andrewhan.nomo.math.vectors.Vector3f
import dev.andrewhan.nomo.math.vectors.minus
import dev.andrewhan.nomo.math.vectors.plus
import dev.andrewhan.nomo.math.vectors.zeroVector2f
import dev.andrewhan.nomo.math.vectors.zeroVector3f
import dev.andrewhan.nomo.sdk.components.Exclusive

interface KineticComponent<VectorType : Vector<Float>> : Component, Exclusive {
  var netForce: VectorType

  fun addForce(force: VectorType) {
    netForce += force
  }

  fun reset() {
    netForce -= netForce
  }
}

class Kinetic2dComponent : KineticComponent<Vector2f> {
  override var netForce: Vector2f = zeroVector2f()

  override fun toString(): String = "${this::class.simpleName}(netForce=$netForce)"
}

class Kinetic3dComponent : KineticComponent<Vector3f> {
  override var netForce: Vector3f = zeroVector3f()

  override fun toString(): String = "${this::class.simpleName}(netForce=$netForce)"
}
