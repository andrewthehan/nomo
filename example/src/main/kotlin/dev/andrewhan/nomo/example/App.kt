package dev.andrewhan.nomo.example

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import dev.andrewhan.nomo.boot.combat.systems.CombatPlugin
import dev.andrewhan.nomo.boot.time.components.DelayedActionComponent
import dev.andrewhan.nomo.boot.time.systems.TimePlugin
import dev.andrewhan.nomo.example.ball.BallComponent
import dev.andrewhan.nomo.example.player.PlayerComponent
import dev.andrewhan.nomo.example.player.PlayerKeyControllerSystem
import dev.andrewhan.nomo.example.player.PlayerMouseControllerSystem
import dev.andrewhan.nomo.integration.libgdx.game
import dev.andrewhan.nomo.integration.libgdx.io.systems.IOPlugin
import dev.andrewhan.nomo.integration.libgdx.physics.Direction
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.createSafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.events.ForceEvent
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.integration.libgdx.physics.systems.PhysicsPlugin
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraComponent
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.io.KeyEvent
import dev.andrewhan.nomo.sdk.io.KeyPressEvent
import dev.andrewhan.nomo.sdk.io.MouseButtonEvent
import dev.andrewhan.nomo.sdk.util.Location
import dev.andrewhan.nomo.sdk.util.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import ktx.async.KTX
import ktx.async.newAsyncContext
import ktx.box2d.box
import ktx.box2d.circle
import ktx.math.times
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, DelicateCoroutinesApi::class)
fun main() {
  Dispatchers.shutdown()
  val updateScope = CoroutineScope(newAsyncContext(8, "UpdateScope"))
  val renderScope = CoroutineScope(Dispatchers.KTX)

  val engine =
    basicEngine(updateScope) {
      apply(CombatPlugin)
      apply(IOPlugin)
      apply(TimePlugin)
      apply(PhysicsPlugin(renderScope))

      forEvent<KeyEvent> { run<PlayerKeyControllerSystem>() }
      forEvent<MouseButtonEvent> { run<PlayerMouseControllerSystem>() }

      forEvent<StartCollisionEvent> { run<BallCollisionSystem>() }

      forEvent<KeyPressEvent> { run<DebugSystem>() }
      forEvent<ComponentRemovedEvent> { run<ShutdownSystem>() }

      constant<TimeStep, Duration> { 1.seconds / 300 }
    }

  val world = createSafeWorld("main world")
  engine.apply {
    "camera" bind CameraComponent(Location(), Size(1366, 768), Vector2(), 0.01f)

    "me" bind PlayerComponent
    "me" bind
      BodyComponent(world) {
        type = BodyType.KinematicBody
        position.set(-5f, 0f)
        fixedRotation = true
        box(width = 0.1f, height = 1f) {
          friction = 0f
          restitution = 1f
        }
      }

    "top wall" bind
      BodyComponent(world) {
        type = BodyType.StaticBody
        position.set(0f, 3f)
        box(width = 1366f, height = 0.1f) {
          friction = 0f
          restitution = 1f
        }
      }

    "bottom wall" bind
      BodyComponent(world) {
        type = BodyType.StaticBody
        position.set(0f, -3f)
        box(width = 1366f, height = 0.1f) {
          friction = 0f
          restitution = 1f
        }
      }

    "right wall" bind
      BodyComponent(world) {
        type = BodyType.StaticBody
        position.set(5f, 0f)
        box(width = 0.1f, height = 768f) {
          friction = 0f
          restitution = 1f
        }
      }

    "ball" bind BallComponent
    "ball" bind
      BodyComponent(world) {
        type = BodyType.DynamicBody
        position.set(0f, 0f)
        circle(radius = 0.1f) {
          friction = 0f
          density = 0.1f
          restitution = 1f
        }
      }
    "ball" bind
      DelayedActionComponent(1.seconds) { entity, engine ->
        val speed = 7f
        val direction = Direction.RIGHT
        // Vector2(Math.random().toFloat(), Math.random().toFloat()).nor()

        engine.dispatchEvent(ForceEvent(direction * speed, entity))
        true
      }
  }

  game(engine).start()
}
