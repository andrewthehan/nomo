package dev.andrewhan.nomo.example

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import dev.andrewhan.nomo.boot.time.systems.TimePlugin
import dev.andrewhan.nomo.boot.xp.events.LevelUpEvent
import dev.andrewhan.nomo.boot.xp.systems.LevelPlugin
import dev.andrewhan.nomo.example.ball.BallCollisionSystem
import dev.andrewhan.nomo.example.ball.newBall
import dev.andrewhan.nomo.example.items.xp.ExperienceItemCollisionSystem
import dev.andrewhan.nomo.example.items.xp.ExperienceItemGenerationSystem
import dev.andrewhan.nomo.example.player.PlayerKeyControllerSystem
import dev.andrewhan.nomo.example.player.PlayerLevelUpSystem
import dev.andrewhan.nomo.example.player.PlayerMouseControllerSystem
import dev.andrewhan.nomo.example.player.newPlayer
import dev.andrewhan.nomo.integration.libgdx.game
import dev.andrewhan.nomo.integration.libgdx.io.systems.IOPlugin
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.DestroySelfOnCollisionComponent
import dev.andrewhan.nomo.integration.libgdx.physics.createSafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.integration.libgdx.physics.systems.PhysicsPlugin
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.engines.key
import dev.andrewhan.nomo.sdk.entities.entity
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
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
      apply(IOPlugin)
      apply(TimePlugin)
      apply(PhysicsPlugin(renderScope))
      apply(LevelPlugin)

      forEvent<KeyEvent> { run<PlayerKeyControllerSystem>() }
      forEvent<MouseButtonEvent> { run<PlayerMouseControllerSystem>() }

      forEvent<StartCollisionEvent> {
        run<BallCollisionSystem>()
        run<ExperienceItemCollisionSystem>()
      }
      forEvent<UpdateEvent> { run<ExperienceItemGenerationSystem>() }
      forEvent<LevelUpEvent> { run<PlayerLevelUpSystem>() }

      forEvent<KeyPressEvent> { run<DebugSystem>() }
      forEvent<ComponentRemovedEvent> { run<ShutdownSystem>() }

      constant<TimeStep, Duration> { 1.seconds / 300 }
      constant<GameBounds, Size> { Size(12f, 6f) }
    }

  val world = createSafeWorld("main world")
  engine.apply {
    "camera" bind CameraComponent(Location(), Size(1366f, 768f), Vector2(), 0.01f)
    newPlayer(world)
    newBall(world)

    newWalls(world)
  }

  game(engine).start()
}

fun NomoEngine.newWalls(world: SafeWorld) {
  val gameBounds = getInstance(key<Size>(GameBounds::class))
  val width = gameBounds.width
  val height = gameBounds.height

  "top wall" bind
    BodyComponent(world) {
      type = BodyType.StaticBody
      position.set(0f, height / 2)
      box(width = width, height = 0.1f) {
        friction = 0f
        restitution = 1f
      }
    }

  "bottom wall" bind
    BodyComponent(world) {
      type = BodyType.StaticBody
      position.set(0f, -height / 2)
      box(width = width, height = 0.1f) {
        friction = 0f
        restitution = 1f
      }
    }

  "right wall" bind
    BodyComponent(world) {
      type = BodyType.StaticBody
      position.set(width / 2, 0f)
      box(width = 0.1f, height = height) {
        friction = 0f
        restitution = 1f
      }
    }

  val segments = 10
  val segmentHeight = height / segments
  repeat(segments) {
    entity(
      BodyComponent(world) {
        type = BodyType.StaticBody
        position.set(-width / 2, (-height / 2) + (it * height / segments) + (segmentHeight / 2))
        box(width = 0.1f, height = height / segments) {
          friction = 0f
          restitution = 1f
        }
      },
      DestroySelfOnCollisionComponent
    )
  }
}
