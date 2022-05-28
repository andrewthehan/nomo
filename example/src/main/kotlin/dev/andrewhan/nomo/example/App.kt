package dev.andrewhan.nomo.example

import com.badlogic.gdx.math.Vector2
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
import dev.andrewhan.nomo.example.walls.newWalls
import dev.andrewhan.nomo.integration.libgdx.game
import dev.andrewhan.nomo.integration.libgdx.io.systems.IOPlugin
import dev.andrewhan.nomo.integration.libgdx.physics.createSafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.integration.libgdx.physics.systems.PhysicsPlugin
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraComponent
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.basicEngine
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
