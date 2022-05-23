package dev.andrewhan.nomo.example

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import dev.andrewhan.nomo.boot.combat.components.ArmorComponent
import dev.andrewhan.nomo.boot.combat.components.HealthComponent
import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.boot.combat.systems.CombatPlugin
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.integration.libgdx.game
import dev.andrewhan.nomo.integration.libgdx.io.systems.IOPlugin
import dev.andrewhan.nomo.integration.libgdx.physics.Direction
import dev.andrewhan.nomo.integration.libgdx.physics.component
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.createSafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.events.ForceEvent
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.integration.libgdx.physics.systems.PhysicsPlugin
import dev.andrewhan.nomo.sdk.components.ComponentPackage
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.entities.entity
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.events.KeyEvent
import dev.andrewhan.nomo.sdk.events.KeyHoldEvent
import dev.andrewhan.nomo.sdk.events.KeyPressEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.isZero
import dev.andrewhan.nomo.sdk.util.max
import dev.andrewhan.nomo.sdk.util.min
import dev.andrewhan.nomo.sdk.util.toFloat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ktx.async.KTX
import ktx.async.newAsyncContext
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.earthGravity
import ktx.math.plus
import ktx.math.times
import javax.inject.Inject
import javax.inject.Qualifier
import kotlin.math.cos
import kotlin.math.sin
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
  val updateScope = CoroutineScope(newAsyncContext(8, "UpdateScope"))
  val renderScope = CoroutineScope(Dispatchers.KTX)

  val engine =
    basicEngine(updateScope) {
      apply(CombatPlugin)
      apply(IOPlugin)
      apply(PhysicsPlugin(renderScope))

      //      forEvent<UpdateEvent> { run<PoisonSystem>() }
      //      forEvent<StartCollisionEvent> { run<PoisonSpreaderSystem>() }

      forEvent<KeyEvent> { run<PlayerControllerSystem>() }
      forEvent<ComponentRemovedEvent> { run<ShutdownSystem>() }

      constant<TimeStep, Duration> { 1.seconds / 300 }
      constant<Poison, Float> { 10f }
    }

  val world = createSafeWorld(earthGravity)
  engine.apply {
    "me" bind HealthComponent(100f)
    "me" bind
      BodyComponent(world) {
        type = BodyType.DynamicBody
        position.set(5f, 5f)
        box(width = 1f, height = 1f) {
          friction = 1f
          density = 0.5f
          restitution = 0.3f
        }
      }
    "me" bind PlayerComponent

    "you" bind
      BodyComponent(world) {
        type = BodyType.DynamicBody
        position.set(5.35f, 6.00f)
        box(width = 1f, height = 1f) {
          friction = 1f
          density = 0.5f
          restitution = 0.5f
        }
      }
    "you" bind HealthComponent(40f)
    "you" bind PoisonComponent(60.seconds)

    "who" bind
      BodyComponent(world) {
        type = BodyType.DynamicBody
        position.set(4.6f, 6f)
        box(width = .7f, height = .7f) {
          friction = 1f
          density = 0.5f
          restitution = 0.3f
        }
      }

    "them" bind
      BodyComponent(world) {
        type = BodyType.DynamicBody
        position.set(4.6f, 7f)
        circle(radius = .6f) {
          friction = 1f
          density = 0.5f
          restitution = 1f
        }
      }

    "other" bind
      BodyComponent(world) {
        type = BodyType.StaticBody
        position.set(6f, 3f)
        box(
          width = 10f,
          height = .2f,
          //          angle = MathUtils.degreesToRadians * -20f
          )
      }
    "other" bind HealthComponent(50f)
    "other" bind ArmorComponent(.25f)
    "other" bind PoisonComponent(60.seconds)
  }

  game(engine) { worldScale = 0.01f }.start()
}

// class DeathLogSystem
// @Inject
// constructor(
//  private val engine: NomoEngine,
//  @EngineCoroutineScope private val scope: CoroutineScope
// ) : NomoSystem<RenderEvent>() {
//  private val batch by lazy { SpriteBatch() }
//  private val font by lazy { BitmapFont().apply { data.setScale(0.5f) } }
//
//  private val deaths = mutableListOf<Entity>()
//
//  override suspend fun start() {
//    engine.flowFor<DeathEvent>().onEach { deaths.add(it.entity) }.launchIn(scope)
//  }
//
//  override suspend fun handle(event: RenderEvent) {
//    val message = buildString {
//      appendLine("Death Log")
//      deaths.forEach { entity -> appendLine("$entity died") }
//    }
//    batch.use(event.camera) { font.draw(it, message, 1f, event.camera.viewportHeight) }
//  }
// }

// class DebugRenderSystem @Inject constructor(private val engine: NomoEngine) :
//  NomoSystem<RenderEvent>() {
//  private val batch by lazy { SpriteBatch() }
//  private val font by lazy { BitmapFont().apply { data.setScale(0.01f) } }
//
//  override suspend fun handle(event: RenderEvent) {
//    val message = buildString {
//      engine.entities.sorted().forEach { entity ->
//        appendLine(entity)
//        engine[entity]
//          .sortedBy { it::class.simpleName }
//          .forEach {
//            appendLine("    ${it.toString().replace("${Component::class.simpleName}", "")}")
//          }
//      }
//    }
//    batch.use(event.camera) { font.draw(it, message, 0f, event.camera.viewportHeight) }
//  }
// }

@Qualifier annotation class Poison

@OptIn(ExperimentalTime::class)
data class PoisonComponent(var duration: Duration) : Component, Exclusive

@OptIn(ExperimentalTime::class)
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

class ShutdownSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<ComponentRemovedEvent>() {
  override suspend fun handle(event: ComponentRemovedEvent) {
    if (engine.entities.isEmpty()) {
      exitProcess(0)
    }
  }
}

object PlayerComponent : Component, Pendant, Exclusive {
  override fun toString(): String = "${this::class.simpleName}"
}

class PlayerControllerSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<KeyEvent>() {
  private val speed = 10f

  override suspend fun handle(event: KeyEvent) {
    val entity = engine.getEntityOrNull(PlayerComponent) ?: return
    when (event) {
      is KeyHoldEvent ->
        when (event.key) {
          Key.UP -> engine.dispatchEvent(ForceEvent(Direction.UP * speed, entity))
          Key.DOWN -> engine.dispatchEvent(ForceEvent(Direction.DOWN * speed, entity))
          Key.LEFT -> engine.dispatchEvent(ForceEvent(Direction.LEFT * speed, entity))
          Key.RIGHT -> engine.dispatchEvent(ForceEvent(Direction.RIGHT * speed, entity))
          else -> {}
        }
      is KeyPressEvent ->
        when (event.key) {
          Key.SPACE -> {
            val body = engine.getComponent<BodyComponent>(entity)
            engine.entity(bulletComponentPackage(body))
          }
          else -> {}
        }
    }
  }
}

fun bulletComponentPackage(bodyComponent: BodyComponent): ComponentPackage {
  val direction = Vector2(cos(bodyComponent.body.angle), sin(bodyComponent.body.angle))
  return ComponentPackage(
    BodyComponent(bodyComponent.world) {
      type = BodyType.KinematicBody
      position.set(bodyComponent.body.position + direction * 1f)
      linearVelocity.set(direction * 5f)
      box(width = .1f, height = .1f) {
        friction = 1f
        density = 1f
        restitution = 1f
      }
    }
  )
}

class PoisonSpreaderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<StartCollisionEvent>() {
  @OptIn(ExperimentalTime::class)
  override suspend fun handle(event: StartCollisionEvent) {
    val (world, contact) = event
    val entityA = engine.getEntity(contact.fixtureA.body.component)
    val entityB = engine.getEntity(contact.fixtureB.body.component)

    val poisonA = engine.getComponentOrNull<PoisonComponent>(entityA)
    val poisonB = engine.getComponentOrNull<PoisonComponent>(entityB)

    if (poisonA == null && poisonB == null) {
      return
    }

    val maxDuration = max(poisonA?.duration ?: ZERO, poisonB?.duration ?: ZERO)

    if (poisonA == null) {
      engine.add(entityA, PoisonComponent(maxDuration))
    } else {
      poisonA.duration = maxDuration
    }

    if (poisonB == null) {
      engine.add(entityB, PoisonComponent(maxDuration))
    } else {
      poisonB.duration = maxDuration
    }
  }
}
