package dev.andrewhan.nomo.example.ball

import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import dev.andrewhan.nomo.boot.time.components.DelayedActionComponent
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.events.ForceEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.entities.entity
import ktx.box2d.circle
import ktx.math.times
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun NomoEngine.newBall(world: SafeWorld): Entity =
  entity(
    BallComponent,
    BodyComponent(world) {
      type = BodyDef.BodyType.DynamicBody
      circle(radius = 0.1f) {
        friction = 0f
        density = 0.1f
        restitution = 1f
      }
    },
    DelayedActionComponent(1.seconds) { entity, engine ->
      val speed = 7f
      val direction = Vector2(1f, random(-.5f, .5f)).nor()

      engine.dispatchEvent(ForceEvent(direction * speed, entity))
      true
    }
  )
