package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive

sealed interface BodyComponent : Component, Exclusive

object StaticBodyComponent : BodyComponent {
  override fun toString(): String = "${this::class.simpleName}"
}

object KinematicBodyComponent : BodyComponent {
  override fun toString(): String = "${this::class.simpleName}"
}

object DynamicBodyComponent : BodyComponent {
  override fun toString(): String = "${this::class.simpleName}"
}
