package dev.andrewhan.nomo.integration.libgdx.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant
import ktx.box2d.BodyDefinition

class BodyComponent(val world: SafeWorld, val bodyDef: BodyDefinition.() -> Unit) :
  Component, Exclusive, Pendant {
  val body
    get() = world.getBody(this)
}
