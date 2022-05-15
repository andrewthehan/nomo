package dev.andrewhan.nomo.example

import dev.andrewhan.nomo.boot.combat.components.ArmorComponent
import dev.andrewhan.nomo.boot.combat.components.HealthComponent
import dev.andrewhan.nomo.boot.combat.components.ShieldComponent
import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.boot.combat.events.DeathEvent
import dev.andrewhan.nomo.boot.combat.systems.ArmorSystem
import dev.andrewhan.nomo.boot.combat.systems.DamageSystem
import dev.andrewhan.nomo.boot.combat.systems.DeathSystem
import dev.andrewhan.nomo.boot.combat.systems.ShieldSystem
import dev.andrewhan.nomo.boot.physics.components.Position2dComponent
import dev.andrewhan.nomo.boot.physics.packages.kinematic2dComponentPackage
import dev.andrewhan.nomo.boot.physics.systems.Physics2dStepSystem
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.integration.libgdx.Game
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.StartEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.interfaces.Exclusive
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.isZero
import dev.andrewhan.nomo.sdk.util.min
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import javax.inject.Qualifier
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
  val engine = basicEngine {
    add<DebugSystem>()
    add<StartSystem>()
    add<DeathSystem>()
    add<ShutdownSystem>()
    add<DamageSystem>()
    add<ArmorSystem>()
    add<ShieldSystem>()
    add<PoisonSystem>()

    bindConstant(Poison::class, 10F)

    add<Physics2dStepSystem>()

    order<DeathEvent, DeathSystem, ShutdownSystem>()
    order<DamageEvent, ShieldSystem, ArmorSystem>()
    order<DamageEvent, ArmorSystem, DamageSystem>()
  }

  Game("Game", 1366, 768, engine).start()
}

@ExperimentalTime
class DebugSystem @Inject constructor(private val engine: NomoEngine) : NomoSystem<UpdateEvent>() {
  private var elapsed: Duration = Duration.ZERO

  override suspend fun handle(event: UpdateEvent) {
    elapsed += event.elapsed

    println("[$elapsed]")
    engine.entities.forEach { entity ->
      println(entity)
      engine[entity].forEach { component -> println("\t$component") }
    }
    println()
  }
}

@ExperimentalTime
class StartSystem @Inject constructor(private val engine: NomoEngine) : NomoSystem<StartEvent>() {
  override suspend fun handle(event: StartEvent) {
    engine.apply {
      "I" bind Position2dComponent()

      "me" bind HealthComponent(100F)
      "me" bind ArmorComponent(.25F)
      "me" bind PoisonComponent(5.seconds)
      "me" bind
        kinematic2dComponentPackage {
          velocity {
            x = 1F
            y = 0.5F
          }
        }

      "you" bind HealthComponent(100F)
      "you" bind ShieldComponent(100F)
      "you" bind PoisonComponent(5.seconds)
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

class ShutdownSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<DeathEvent>() {
  override suspend fun handle(event: DeathEvent) {
    if (engine.entities.isEmpty()) {
      engine.stop()
    }
  }
}
