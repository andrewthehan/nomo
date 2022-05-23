package dev.andrewhan.nomo.example

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import dev.andrewhan.nomo.boot.combat.components.ArmorComponent
import dev.andrewhan.nomo.boot.combat.components.HealthComponent
import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.boot.combat.events.DeathEvent
import dev.andrewhan.nomo.boot.combat.systems.ArmorSystem
import dev.andrewhan.nomo.boot.combat.systems.DamageSystem
import dev.andrewhan.nomo.boot.combat.systems.RemoveOnDeathSystem
import dev.andrewhan.nomo.boot.combat.systems.ShieldSystem
import dev.andrewhan.nomo.boot.physics.events.Force2dEvent
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.Game
import dev.andrewhan.nomo.integration.libgdx.components.WorldBodyComponent
import dev.andrewhan.nomo.integration.libgdx.components.WorldComponent
import dev.andrewhan.nomo.integration.libgdx.events.RenderEvent
import dev.andrewhan.nomo.integration.libgdx.systems.ClearRenderSystem
import dev.andrewhan.nomo.integration.libgdx.systems.KeyInputSystem
import dev.andrewhan.nomo.integration.libgdx.systems.WorldPlugin
import dev.andrewhan.nomo.math.vectors.DOWN
import dev.andrewhan.nomo.math.vectors.LEFT
import dev.andrewhan.nomo.math.vectors.RIGHT
import dev.andrewhan.nomo.math.vectors.UP
import dev.andrewhan.nomo.math.vectors.times
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant
import dev.andrewhan.nomo.sdk.engines.EngineCoroutineScope
import dev.andrewhan.nomo.sdk.engines.EnginePlugin
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.KeyEvent
import dev.andrewhan.nomo.sdk.events.KeyHoldEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.stores.flowFor
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.isZero
import dev.andrewhan.nomo.sdk.util.min
import dev.andrewhan.nomo.sdk.util.toFloat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ktx.async.KTX
import ktx.async.newAsyncContext
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import ktx.graphics.use
import javax.inject.Inject
import javax.inject.Qualifier
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

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
      apply(WorldPlugin(renderScope))

      forEvent<UpdateEvent> { run<PoisonSystem>() }
      forEvent<KeyEvent> { run<PlayerControllerSystem>() }
      forEvent<DeathEvent> { run<RemoveOnDeathSystem>() then run<ShutdownSystem>() }
      forEvent<RenderEvent>(renderScope) {
        run<ClearRenderSystem>().apply {
          this then run<DebugRenderSystem>()
          this then run<DeathLogSystem>()
        }
      }

      constant<TimeStep, Duration> { 1.seconds / 300 }
      constant<Poison, Float> { 10f }
    }

  val world = createWorld(gravity = earthGravity.scl(100f, 100f))
  world.setContactListener(
    object : ContactListener {
      override fun beginContact(contact: Contact) {
//        println("beginContact: $contact")
      }

      override fun endContact(contact: Contact) {
//        println("endContact: $contact")
      }

      override fun preSolve(contact: Contact, oldManifold: Manifold) {
//        println("preSolve: $contact")
      }

      override fun postSolve(contact: Contact, impulse: ContactImpulse) {
//        println("postSolve: $contact")
      }
    }
  )
  engine.apply {
    "world" bind WorldComponent(world)

    "me" bind
      WorldBodyComponent(
        world,
        world.body {
          type = BodyType.DynamicBody
          position.set(500f, 500f)
          box(width = 50f, height = 50f) {
            friction = 1f
            density = 0.5f
            restitution = 0.3f
          }
        }
      )
    "me" bind PlayerComponent

    "you" bind
      WorldBodyComponent(
        world,
        world.body {
          type = BodyType.DynamicBody
          position.set(525f, 600f)
          box(width = 50f, height = 50f) {
            friction = 1f
            density = 0.5f
            restitution = 0.5f
          }
        }
      )
    "you" bind HealthComponent(50f)
    "you" bind PoisonComponent(60.seconds)

    "other" bind
      WorldBodyComponent(
        world,
        world.body {
          type = BodyType.StaticBody
          position.set(600f, 100f)
          box(width = 1000f, height = 20f)
        }
      )
    "other" bind HealthComponent(50f)
    "other" bind ArmorComponent(.25f)
    "other" bind PoisonComponent(60.seconds)
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
      appendLine("Death Log")
      deaths.forEach { entity -> appendLine("$entity died") }
    }
    batch.use(event.camera) { font.draw(it, message, 1000f, event.camera.viewportHeight) }
  }
}

class DebugRenderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<RenderEvent>() {
  private val batch by lazy { SpriteBatch() }
  private val font by lazy { BitmapFont() }

  override suspend fun handle(event: RenderEvent) {
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

class ShutdownSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<DeathEvent>() {
  override suspend fun handle(event: DeathEvent) {
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
