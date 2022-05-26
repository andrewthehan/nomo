package dev.andrewhan.nomo.example

import com.badlogic.gdx.math.Vector2
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
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraComponent
import dev.andrewhan.nomo.integration.libgdx.render.systems.CameraFollowBodySystem
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.io.KeyEvent
import dev.andrewhan.nomo.sdk.io.KeyPressEvent
import dev.andrewhan.nomo.sdk.io.MouseButtonEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.Location
import dev.andrewhan.nomo.sdk.util.Size
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

      forEvent<UpdateEvent> { run<CameraFollowBodySystem>() }

      forEvent<KeyPressEvent> { run<DebugSystem>() }

      forEvent<KeyEvent> { run<PlayerKeyControllerSystem>() }
      forEvent<MouseButtonEvent> { run<PlayerMouseControllerSystem>() }
      forEvent<ComponentRemovedEvent> { run<ShutdownSystem>() }

      constant<TimeStep, Duration> { 1.seconds / 300 }
    }

  val world = createSafeWorld("main world", earthGravity)
  engine.apply {
    "minimap" bind
      CameraComponent(
        Location(1366 * 3 / 4, 768 * 3 / 4),
        Size(1366 / 4, 768 / 4),
        Vector2(),
        0.08f
      )

    "me" bind PlayerComponent
    "me" bind
      BodyComponent(world) {
        type = BodyType.DynamicBody
        position.set(0f, 2f)
        box(width = 0.5f, height = 1f) {
          friction = 1f
          density = 0.5f
          restitution = 0.3f
        }
      }
    "me" bind CameraComponent(Location(), Size(1366, 768), Vector2(), 0.01f)

    "floor" bind
      BodyComponent(world) {
        type = BodyType.StaticBody
        position.set(0f, -2f)
        box(width = 10f, height = 0.2f)
      }
  }

  game(engine).start()
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
