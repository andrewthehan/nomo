package dev.andrewhan.nomo.integration.libgdx.components

import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant
import ktx.box2d.BodyDefinition
import ktx.box2d.body

class WorldBodyComponent(world: World, bodyDef: BodyDefinition.() -> Unit) :
  Component, Exclusive, Pendant {
  val body = world.body(init = bodyDef)

  override fun toString(): String =
    "${this::class.simpleName}(position=${body.position},velocity=${body.linearVelocity})"
}
