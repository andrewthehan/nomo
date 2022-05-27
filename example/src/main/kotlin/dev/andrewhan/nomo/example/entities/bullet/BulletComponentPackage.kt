package dev.andrewhan.nomo.example.entities.bullet

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import dev.andrewhan.nomo.boot.time.components.DelayedActionComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.sdk.components.ComponentPackage
import ktx.box2d.circle
import ktx.math.plus
import ktx.math.times
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun bulletComponentPackage(bodyComponent: BodyComponent): ComponentPackage {
  val direction = Vector2(cos(bodyComponent.body.angle), sin(bodyComponent.body.angle))
  return ComponentPackage(
    BodyComponent(bodyComponent.world) {
      type = BodyDef.BodyType.KinematicBody
      position.set(bodyComponent.body.position + direction * 1f)
      linearVelocity.set(direction * 2f)
      circle(radius = .05f)
    },
    DelayedActionComponent(1.seconds) { entity, engine -> engine.remove(entity).isNotEmpty() }
  )
}
