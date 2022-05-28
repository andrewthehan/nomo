package dev.andrewhan.nomo.integration.libgdx.physics.components

import com.badlogic.gdx.physics.box2d.Body
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant
import ktx.box2d.BodyDefinition

class BodyComponent(val world: SafeWorld, val bodyDef: BodyDefinition.() -> Unit) :
  Component, Exclusive, Pendant {
  val body: Body?
    get() = world.getBody(this)

  override fun toString(): String = "${this::class.simpleName}(world=$world)"
}
