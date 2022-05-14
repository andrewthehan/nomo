package dev.andrewhan.nomo.example

import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.boot.combat.events.DeathEvent
import dev.andrewhan.nomo.boot.combat.systems.ArmorSystem
import dev.andrewhan.nomo.boot.combat.systems.DamageSystem
import dev.andrewhan.nomo.boot.combat.systems.DeathSystem
import dev.andrewhan.nomo.boot.combat.systems.ShieldSystem
import dev.andrewhan.nomo.boot.physics.components.Acceleration2dComponent
import dev.andrewhan.nomo.boot.physics.components.Position2dComponent
import dev.andrewhan.nomo.boot.physics.components.Velocity2dComponent
import dev.andrewhan.nomo.boot.physics.systems.Physics2dStepSystem
import dev.andrewhan.nomo.integration.libgdx.Game
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.basicEngine
import dev.andrewhan.nomo.sdk.events.StartEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import kotlin.time.Duration
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
    //    add<PoisonSystem>()
    //    add<StrongPoisonSystem>()

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

class StartSystem @Inject constructor(private val engine: NomoEngine) : NomoSystem<StartEvent>() {
  override suspend fun handle(event: StartEvent) {
    //    engine.add("me", HealthComponent(100F))
    //    engine.add("me", ArmorComponent(.25F))
    engine.add("me", Position2dComponent())
    engine.add("me", Velocity2dComponent(1F, 0.5F))
    engine.add("me", Acceleration2dComponent())

    //    engine.add("you", HealthComponent(100F))
    //    engine.add("you", ShieldComponent(100F))
  }
}

@ExperimentalTime
class StrongPoisonSystem @Inject constructor(engine: NomoEngine) : PoisonSystem(engine)

@ExperimentalTime
open class PoisonSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  private val dps = 10

  override suspend fun handle(event: UpdateEvent) {
    engine.entities.forEach {
      engine.dispatchEvent(DamageEvent(event.elapsed.toFloat(DurationUnit.SECONDS) * dps, it))
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
