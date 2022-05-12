package dev.andrewhan.nomo.example

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import dev.andrewhan.nomo.integration.libgdx.Game
import dev.andrewhan.nomo.sdk.BasicEngine
import dev.andrewhan.nomo.sdk.engine
import dev.andrewhan.nomo.sdk.events.StartEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.interfaces.Exclusive
import dev.andrewhan.nomo.sdk.stores.getComponent
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
  val engine = engine {
    add<StartSystem>()
    add<PoisonSystem>()
    add<DamageSystem>()
    add<DebugSystem>()
    add<TestSystem>()
  }
  Game("Game", 1366, 768, engine).start()
}

@ExperimentalTime
class TestSystem : System<UpdateEvent> {
  override suspend fun handle(event: UpdateEvent) {
    println(event.elapsed)
  }
}

class DebugSystem @Inject constructor(private val engine: BasicEngine) : System<UpdateEvent> {
  override suspend fun handle(event: UpdateEvent) {
    println("entities: ${engine.entities}")
    println("components: ${engine.components}")
  }
}

class StartSystem @Inject constructor(private val engine: BasicEngine) : System<StartEvent> {
  override suspend fun handle(event: StartEvent) {
    engine.add(Entity("me"), HealthComponent(100.0))
  }
}

data class HealthComponent(var health: Double) : Component, Exclusive

data class DamageEvent(val damage: Double, val entities: Iterable<Entity>) : Event

@ExperimentalTime
class PoisonSystem @Inject constructor(private val engine: BasicEngine) : System<UpdateEvent> {
  override suspend fun handle(event: UpdateEvent) {
    engine.dispatchEvent(
      DamageEvent(event.elapsed.toDouble(DurationUnit.SECONDS) * 10, engine.entities)
    )
  }
}

class DamageSystem @Inject constructor(private val engine: BasicEngine) : System<DamageEvent> {
  override suspend fun handle(event: DamageEvent) {
    event.entities
      .map { engine.getComponent<HealthComponent>(it)!! }
      .forEach { it.health -= event.damage }
  }
}
