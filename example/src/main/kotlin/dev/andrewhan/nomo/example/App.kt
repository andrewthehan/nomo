package dev.andrewhan.nomo.example

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import dev.andrewhan.nomo.boot.combat.systems.CombatPlugin
import dev.andrewhan.nomo.boot.time.systems.TimePlugin
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.example.entities.player.PlayerComponent
import dev.andrewhan.nomo.example.entities.player.PlayerKeyControllerSystem
import dev.andrewhan.nomo.example.entities.player.PlayerMouseControllerSystem
import dev.andrewhan.nomo.integration.libgdx.game
import dev.andrewhan.nomo.integration.libgdx.io.systems.IOPlugin
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.createSafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.systems.PhysicsPlugin
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.io.KeyEvent
import dev.andrewhan.nomo.sdk.io.KeyPressEvent
import dev.andrewhan.nomo.sdk.io.MouseButtonEvent
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ktx.async.KTX
import ktx.async.newAsyncContext
import ktx.box2d.box
import ktx.box2d.earthGravity
import javax.inject.Inject
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
  val updateScope = CoroutineScope(newAsyncContext(8, "UpdateScope"))
  val renderScope = CoroutineScope(Dispatchers.KTX)

  val engine =
    basicEngine(updateScope) {
      apply(CombatPlugin)
      apply(IOPlugin)
      apply(TimePlugin)
      apply(PhysicsPlugin(renderScope))

      forEvent<KeyPressEvent> { run<DebugSystem>() }

      forEvent<KeyEvent> { run<PlayerKeyControllerSystem>() }
      forEvent<MouseButtonEvent> { run<PlayerMouseControllerSystem>() }
      forEvent<ComponentRemovedEvent> { run<ShutdownSystem>() }

      constant<TimeStep, Duration> { 1.seconds / 300 }
    }

  val world = createSafeWorld(earthGravity)
  engine.apply {
    "me" bind
      BodyComponent(world) {
        type = BodyType.DynamicBody
        position.set(5f, 5f)
        box(width = 0.5f, height = 1f) {
          friction = 1f
          density = 0.5f
          restitution = 0.3f
        }
      }
    "me" bind PlayerComponent

    "floor" bind
      BodyComponent(world) {
        type = BodyType.StaticBody
        position.set(5f, 2f)
        box(width = 10f, height = 0.2f)
      }
  }

  game(engine) { worldScale = 0.01f }.start()
}

class DebugSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<KeyPressEvent>() {
  override suspend fun handle(event: KeyPressEvent) {
    when (event.key) {
      Key.T -> {
        val message = buildString {
          engine.entities.sorted().forEach { entity ->
            appendLine(entity)
            engine[entity]
              .sortedBy { it::class.simpleName }
              .forEach {
                appendLine("    ${it.toString().replace("${Component::class.simpleName}", "")}")
              }
          }
        }
        println(message)
      }
      else -> {}
    }
  }
}

class ShutdownSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<ComponentRemovedEvent>() {
  override suspend fun handle(event: ComponentRemovedEvent) {
    if (engine.entities.isEmpty()) {
      exitProcess(0)
    }
  }
}
