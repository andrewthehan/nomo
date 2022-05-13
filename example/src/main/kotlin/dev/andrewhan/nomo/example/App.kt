package dev.andrewhan.nomo.example

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.integration.libgdx.Game
import dev.andrewhan.nomo.sdk.BasicEngine
import dev.andrewhan.nomo.sdk.engine
import dev.andrewhan.nomo.sdk.events.StartEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.interfaces.Exclusive
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
  val engine = engine {
    add<DebugSystem>()
    add<StartSystem>()
    add<DeathSystem>()
    add<ShutdownSystem>()
    add<DamageSystem>()
    add<ArmorSystem>()
    add<ShieldSystem>()
    add<PoisonSystem>()
    add<StrongPoisonSystem>()

    order<DeathEvent, DeathSystem, ShutdownSystem>()
    order<DamageEvent, ShieldSystem, ArmorSystem>()
    order<DamageEvent, ArmorSystem, DamageSystem>()
  }
  Game("Game", 1366, 768, engine).start()
}

class DebugSystem @Inject constructor(private val engine: BasicEngine) : NomoSystem<UpdateEvent>() {
  override suspend fun handle(event: UpdateEvent) {
    engine.entities.forEach { println("$it (${engine[it]})") }
    println()
  }
}

class StartSystem @Inject constructor(private val engine: BasicEngine) : NomoSystem<StartEvent>() {
  override suspend fun handle(event: StartEvent) {
    engine.add("me", HealthComponent(100.0))
    engine.add("me", ArmorComponent(.5))
    engine.add("you", HealthComponent(100.0))
    engine.add("you", ShieldComponent(100.0))
  }
}

data class HealthComponent(var health: Double, val maxHealth: Double = Double.MAX_VALUE) :
  Component, Exclusive {
  fun isAlive(): Boolean = health > 0.0
  fun isDead(): Boolean = health == 0.0

  fun damage(amount: Double) {
    if (amount == 0.0) {
      return
    }

    require(amount >= 0.0) { "Damage amount should be non-negative: $amount" }

    health = max(health - amount, 0.0)
  }

  fun heal(amount: Double) {
    if (amount == 0.0) {
      return
    }

    require(amount >= 0.0) { "Heal amount should be non-negative: $amount" }

    health = min(health + amount, maxHealth)
  }
}

data class DamageEvent(var damage: Double, val entity: Entity) : Event

@ExperimentalTime
class StrongPoisonSystem @Inject constructor(engine: BasicEngine) : PoisonSystem(engine)

@ExperimentalTime
open class PoisonSystem @Inject constructor(private val engine: BasicEngine) :
  NomoSystem<UpdateEvent>() {
  override suspend fun handle(event: UpdateEvent) {
    engine.entities.forEach {
      engine.dispatchEvent(DamageEvent(event.elapsed.toDouble(DurationUnit.SECONDS) * 10, it))
    }
  }
}

class DamageSystem @Inject constructor(private val engine: BasicEngine) :
  NomoSystem<DamageEvent>() {
  override suspend fun handle(event: DamageEvent) {
    engine.getComponentOrNull<HealthComponent>(event.entity)?.let {
      it.damage(event.damage)

      if (it.isDead()) {
        engine.dispatchEvent(DeathEvent(event.entity))
      }
    }
  }
}

data class ArmorComponent(val reduction: Double) : Component, Exclusive

class ArmorSystem @Inject constructor(private val engine: BasicEngine) : NomoSystem<DamageEvent>() {
  override suspend fun handle(event: DamageEvent) {
    engine.getComponentOrNull<ArmorComponent>(event.entity)?.apply {
      event.damage *= (1 - reduction)
    }
  }
}

data class ShieldComponent(var amount: Double) : Component, Exclusive {
  val isDepleted get() = amount <= 0.0

  fun absorb(damage: Double): Double {
    val damageMitigated = min(damage, amount)

    amount -= damageMitigated

    return damage - damageMitigated
  }
}

class ShieldSystem @Inject constructor(private val engine: BasicEngine) :
  NomoSystem<DamageEvent>(propagate = false) {
  override suspend fun handle(event: DamageEvent) {
    val shield = engine.getComponentOrNull<ShieldComponent>(event.entity)

    if (shield == null) {
      emit(event)
      return
    }

    val damageRemaining = shield.absorb(event.damage)
    event.damage = damageRemaining

    if (shield.isDepleted) {
      engine.remove(shield)
    }

    if (event.damage > 0) {
      emit(event)
    }
  }
}

data class DeathEvent(val entity: Entity) : Event

class DeathSystem @Inject constructor(private val engine: BasicEngine) : NomoSystem<DeathEvent>() {
  override suspend fun handle(event: DeathEvent) {
    engine.remove(event.entity)
  }
}

class ShutdownSystem @Inject constructor(private val engine: BasicEngine) :
  NomoSystem<DeathEvent>() {
  override suspend fun handle(event: DeathEvent) {
    if (engine.entities.isEmpty()) {
      exitProcess(0)
    }
  }
}
