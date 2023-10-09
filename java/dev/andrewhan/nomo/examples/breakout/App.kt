package dev.andrewhan.nomo.examples.breakout

import dev.andrewhan.nomo.boot.time.systems.TimePlugin
import dev.andrewhan.nomo.boot.xp.events.LevelUpEvent
import dev.andrewhan.nomo.boot.xp.systems.LevelPlugin
import dev.andrewhan.nomo.examples.breakout.ball.BallCollisionSystem
import dev.andrewhan.nomo.examples.breakout.items.xp.ExperienceItemCollisionSystem
import dev.andrewhan.nomo.examples.breakout.items.xp.ExperienceItemGenerationSystem
import dev.andrewhan.nomo.examples.breakout.player.PlayerKeyControllerSystem
import dev.andrewhan.nomo.examples.breakout.player.PlayerLevelUpSystem
import dev.andrewhan.nomo.examples.breakout.player.PlayerMouseControllerSystem
import dev.andrewhan.nomo.examples.breakout.walls.WallSegmentRegenerateEvent
import dev.andrewhan.nomo.examples.breakout.walls.WallSegmentRegenerationSystem
import dev.andrewhan.nomo.integration.libgdx.game
import dev.andrewhan.nomo.integration.libgdx.io.systems.IOPlugin
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.integration.libgdx.physics.systems.PhysicsPlugin
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.io.KeyEvent
import dev.andrewhan.nomo.sdk.io.KeyPressEvent
import dev.andrewhan.nomo.sdk.io.MouseButtonEvent
import dev.andrewhan.nomo.sdk.util.Size
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import ktx.async.KTX
import ktx.async.newAsyncContext

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

        forEvent<NeverEvent> { run<StartUpSystem>() }

        forEvent<KeyEvent> { run<PlayerKeyControllerSystem>() }
        forEvent<MouseButtonEvent> { run<PlayerMouseControllerSystem>() }

        forEvent<StartCollisionEvent> {
          run<BallCollisionSystem>()
          run<ExperienceItemCollisionSystem>()
        }
        forEvent<UpdateEvent> { run<ExperienceItemGenerationSystem>() }
        forEvent<LevelUpEvent> { run<PlayerLevelUpSystem>() }
        forEvent<WallSegmentRegenerateEvent> { run<WallSegmentRegenerationSystem>() }

        forEvent<KeyPressEvent> { run<DebugSystem>() }
        forEvent<ComponentRemovedEvent> { run<ShutdownSystem>() }

        constant<TimeStep, Duration> { 1.seconds / 300 }
        constant<GameBounds, Size> { Size(12f, 6f) }
      }

  game(engine).start()
}
