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
import dev.andrewhan.nomo.boot.physics.components.DynamicBodyComponent
import dev.andrewhan.nomo.boot.physics.components.Kinetic2dComponent
import dev.andrewhan.nomo.boot.physics.components.MassComponent
import dev.andrewhan.nomo.boot.physics.components.Position2dComponent
import dev.andrewhan.nomo.boot.physics.components.ShapeComponent
import dev.andrewhan.nomo.boot.physics.events.Force2dEvent
import dev.andrewhan.nomo.boot.physics.packages.kinematic2dComponentPackage
import dev.andrewhan.nomo.boot.physics.systems.Physics2dStepSystem
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.Game
import dev.andrewhan.nomo.integration.libgdx.components.WorldComponent
import dev.andrewhan.nomo.integration.libgdx.events.RenderEvent
import dev.andrewhan.nomo.integration.libgdx.systems.ClearRenderSystem
import dev.andrewhan.nomo.integration.libgdx.systems.KeyInputSystem
import dev.andrewhan.nomo.integration.libgdx.systems.WorldRenderSystem
import dev.andrewhan.nomo.integration.libgdx.systems.WorldStepSystem
import dev.andrewhan.nomo.math.shapes.Circle
import dev.andrewhan.nomo.math.shapes.Rectangle
import dev.andrewhan.nomo.math.shapes.RegularPolygon
import dev.andrewhan.nomo.math.vectors.DOWN
import dev.andrewhan.nomo.math.vectors.LEFT
import dev.andrewhan.nomo.math.vectors.RIGHT
import dev.andrewhan.nomo.math.vectors.UP
import dev.andrewhan.nomo.math.vectors.plus
import dev.andrewhan.nomo.math.vectors.times
import dev.andrewhan.nomo.math.vectors.zeroVector2f
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant
import dev.andrewhan.nomo.sdk.engines.EngineCoroutineScope
import dev.andrewhan.nomo.sdk.engines.EnginePlugin
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.KeyEvent
import dev.andrewhan.nomo.sdk.events.KeyHoldEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.stores.flowFor
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.isZero
import dev.andrewhan.nomo.sdk.util.min
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import javax.inject.Qualifier
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ktx.async.KTX
import ktx.async.newAsyncContext
import ktx.box2d.createWorld
import ktx.graphics.use

val CombatPlugin: EnginePlugin = {
  forEvent<DamageEvent> { run<ShieldSystem>() then run<ArmorSystem>() then run<DamageSystem>() }
}

val IOPlugin: EnginePlugin = { forEvent<UpdateEvent> { run<KeyInputSystem>() } }

@OptIn(ExperimentalTime::class)
fun main() {
  val updateScope = CoroutineScope(newAsyncContext(8, "UpdateScope"))
  val renderScope = CoroutineScope(Dispatchers.KTX)

  val engine =
    basicEngine(updateScope) {
      apply(CombatPlugin)
      apply(IOPlugin)
      forEvent<UpdateEvent> {
        run<PoisonSystem>()
        run<WorldStepSystem>()
        run<Physics2dStepSystem>()
      }
      forEvent<KeyEvent> { run<PlayerControllerSystem>() }
      forEvent<DeathEvent> { run<DeathSystem>() then run<ShutdownSystem>() }
      forEvent<RenderEvent>(renderScope) {
        run<ClearRenderSystem>().apply {
          this then run<ShapeRenderSystem>()
          this then run<DebugRenderSystem>()
          this then run<DeathLogSystem>()

          this then run<WorldRenderSystem>()
        }
      }
      forEvent<Force2dEvent> { run<ForceApplicationSystem>() }

      constant<Poison, Float> { 10f }
    }

  engine.apply {
    "world" bind WorldComponent(createWorld())

    "me" bind PlayerComponent
    "me" bind kinematic2dComponentPackage {}
    "me" bind Kinetic2dComponent()
    "me" bind MassComponent(1f)
    "me" bind DynamicBodyComponent()
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

    "other" bind HealthComponent(50f)
    "other" bind ArmorComponent(.25f)
    "other" bind PoisonComponent(60.seconds)
    "other" bind
      kinematic2dComponentPackage {
        velocity {
          x = 100f
          y = 100f
        }
      }
    "other" bind ShapeComponent(RegularPolygon(zeroVector2f(), 20f, 7))
  }

  Game("Game", 1366, 768, engine).start()
}

class DeathLogSystem
@Inject
constructor(
  private val engine: NomoEngine,
  @EngineCoroutineScope private val scope: CoroutineScope
) : NomoSystem<RenderEvent>() {
  private val batch by lazy { SpriteBatch() }
  private val font by lazy { BitmapFont() }

  private val deaths = mutableListOf<Entity>()

  override suspend fun start() {
    engine.flowFor<DeathEvent>().onEach { deaths.add(it.entity) }.launchIn(scope)
  }

  override suspend fun handle(event: RenderEvent) {
    val message = buildString {
      appendLine("DEATH LOG")
      deaths.forEach { entity -> appendLine("$entity died") }
    }
    batch.use(event.camera) { font.draw(it, message, 800f, event.camera.viewportHeight) }
  }
}

class DebugRenderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<RenderEvent>() {
  private val batch by lazy { SpriteBatch() }
  private val font by lazy { BitmapFont() }

  override suspend fun handle(event: RenderEvent) {
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
  NomoSystem<RenderEvent>() {
  private val shapeRenderer by lazy { ShapeRenderer() }

  override suspend fun handle(event: RenderEvent) {
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
      exitProcess(0)
    }
  }
}

object PlayerComponent : Component, Pendant, Exclusive

class PlayerControllerSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<KeyEvent>() {
  private val speed = 100f

  override suspend fun handle(event: KeyEvent) {
    val entity = engine.getEntity(PlayerComponent)
    when (event) {
      is KeyHoldEvent ->
        when (event.key) {
          Key.UP -> engine.dispatchEvent(Force2dEvent(UP * speed, entity))
          Key.DOWN -> engine.dispatchEvent(Force2dEvent(DOWN * speed, entity))
          Key.LEFT -> engine.dispatchEvent(Force2dEvent(LEFT * speed, entity))
          Key.RIGHT -> engine.dispatchEvent(Force2dEvent(RIGHT * speed, entity))
          else -> {}
        }
    }
  }
}

class ForceApplicationSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<Force2dEvent>() {
  override suspend fun handle(event: Force2dEvent) {
    engine.getComponentOrNull<Kinetic2dComponent>(event.entity)?.addForce(event.newtons)
  }
}
