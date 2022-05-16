package dev.andrewhan.nomo.example

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.andrewhan.nomo.boot.combat.components.ArmorComponent
import dev.andrewhan.nomo.boot.combat.components.HealthComponent
import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.boot.combat.events.DeathEvent
import dev.andrewhan.nomo.boot.combat.systems.ArmorSystem
import dev.andrewhan.nomo.boot.combat.systems.DamageSystem
import dev.andrewhan.nomo.boot.combat.systems.DeathSystem
import dev.andrewhan.nomo.boot.combat.systems.ShieldSystem
import dev.andrewhan.nomo.boot.physics.components.Position2dComponent
import dev.andrewhan.nomo.boot.physics.components.ShapeComponent
import dev.andrewhan.nomo.boot.physics.packages.kinematic2dComponentPackage
import dev.andrewhan.nomo.boot.physics.systems.Physics2dStepSystem
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.Game
import dev.andrewhan.nomo.integration.libgdx.events.LibgdxRenderEvent
import dev.andrewhan.nomo.integration.libgdx.systems.ClearRenderSystem
import dev.andrewhan.nomo.math.shapes.Circle
import dev.andrewhan.nomo.math.shapes.Rectangle
import dev.andrewhan.nomo.math.shapes.RegularPolygon
import dev.andrewhan.nomo.math.vectors.plus
import dev.andrewhan.nomo.math.vectors.zeroVector2f
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.StartEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.interfaces.Exclusive
import dev.andrewhan.nomo.sdk.stores.flowFor
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.isZero
import dev.andrewhan.nomo.sdk.util.min
import dev.andrewhan.nomo.sdk.util.toFloat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ktx.graphics.use
import javax.inject.Inject
import javax.inject.Qualifier
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
  val engine = basicEngine {
    add<StartSystem>()
    add<DeathSystem>()
    add<ShutdownSystem>()
    add<DamageSystem>()
    add<ArmorSystem>()
    add<ShieldSystem>()
    add<PoisonSystem>()

    add<ClearRenderSystem>()
    add<ShapeRenderSystem>()
    order<LibgdxRenderEvent, ClearRenderSystem, ShapeRenderSystem>()
    add<DebugRenderSystem>()
    order<LibgdxRenderEvent, ClearRenderSystem, DebugRenderSystem>()
    add<DeathLogSystem>()
    order<LibgdxRenderEvent, ClearRenderSystem, DeathLogSystem>()

    bindConstant(Poison::class, 10f)

    add<Physics2dStepSystem>()

    order<DeathEvent, DeathSystem, ShutdownSystem>()
    order<DamageEvent, ShieldSystem, ArmorSystem>()
    order<DamageEvent, ArmorSystem, DamageSystem>()
  }

  Game("Game", 1366, 768, engine).start()
}

class DeathLogSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<LibgdxRenderEvent>() {
  private val batch by lazy { SpriteBatch() }
  private val font by lazy { BitmapFont() }

  private val deaths = mutableListOf<Entity>()

  override suspend fun start(scope: CoroutineScope, events: Flow<LibgdxRenderEvent>): Job {
    // TODO: hack running in the wrong scope
    engine.flowFor<DeathEvent>().onEach { deaths.add(it.entity) }.launchIn(scope)
    return super.start(scope, events)
  }

  override suspend fun handle(event: LibgdxRenderEvent) {
    val message = buildString {
      appendLine("DEATH LOG")
      deaths.forEach { entity -> appendLine("$entity died") }
    }
    batch.use(event.camera) { font.draw(it, message, 800f, event.camera.viewportHeight) }
  }
}

class DebugRenderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<LibgdxRenderEvent>() {
  private val batch by lazy { SpriteBatch() }
  private val font by lazy { BitmapFont() }

  override suspend fun handle(event: LibgdxRenderEvent) {
    val message = buildString {
      engine.entities.forEach { entity ->
        appendLine(entity)
        engine[entity].forEach { component ->
          appendLine("    $component".replace("${Component::class.simpleName}", ""))
        }
      }
    }
    batch.use(event.camera) { font.draw(it, message, 0f, event.camera.viewportHeight) }
  }
}

@ExperimentalTime
class StartSystem @Inject constructor(private val engine: NomoEngine) : NomoSystem<StartEvent>() {
  override suspend fun handle(event: StartEvent) {
    engine.apply {
      "me" bind HealthComponent(50f)
      "me" bind ArmorComponent(.25f)
      "me" bind PoisonComponent(60.seconds)
      "me" bind
        kinematic2dComponentPackage {
          velocity {
            x = 100f
            y = 50f
          }
        }
      "me" bind ShapeComponent(Rectangle(zeroVector2f(), 20f, 20f))

      "you" bind HealthComponent(20f)
      "you" bind PoisonComponent(60.seconds)
      "you" bind
        kinematic2dComponentPackage {
          velocity {
            x = 30f
            y = 100f
          }
        }
      "you" bind ShapeComponent(Circle(zeroVector2f(), 10f))

      "other" bind
        kinematic2dComponentPackage {
          velocity {
            x = 100f
            y = 100f
          }
        }
      "other" bind ShapeComponent(RegularPolygon(zeroVector2f(), 20f, 7))
    }
  }
}

@Qualifier annotation class Poison

@ExperimentalTime data class PoisonComponent(var duration: Duration) : Component, Exclusive

@ExperimentalTime
open class PoisonSystem
@Inject
constructor(private val engine: NomoEngine, @Poison private val dps: Float) :
  NomoSystem<UpdateEvent>() {

  override suspend fun handle(event: UpdateEvent) {
    engine.getComponents<PoisonComponent>().forEach { poison ->
      val minDuration = min(poison.duration, event.elapsed)
      poison.duration -= minDuration

      if (poison.duration.isZero) {
        engine.remove(poison)
      }

      val damage = minDuration.toFloat(DurationUnit.SECONDS) * dps
      engine[poison].forEach { entity -> engine.dispatchEvent(DamageEvent(damage, entity)) }
    }
  }
}

class ShapeRenderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<LibgdxRenderEvent>() {
  private val shapeRenderer by lazy { ShapeRenderer() }

  override suspend fun handle(event: LibgdxRenderEvent) {
    engine.getComponents<ShapeComponent>().forEach { shapeComponent ->
      val entity = engine.getEntityOrNull(shapeComponent) ?: return
      val position = engine.getComponentOrNull<Position2dComponent>(entity) ?: return
      shapeRenderer.use(ShapeRenderer.ShapeType.Filled, event.camera) { renderer ->
        val points = shapeComponent.shape.points.map { it + position }
        points.zipWithNext { a, b -> renderer.line(a.x, a.y, b.x, b.y) }
        if (points.size > 2) {
          val first = points.first()
          val last = points.last()
          renderer.line(last.x, last.y, first.x, first.y)
        }
      }
    }
  }
}

class ShutdownSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<DeathEvent>() {
  override suspend fun handle(event: DeathEvent) {
    if (engine.entities.isEmpty()) {
      engine.stop()
    }
  }
}
