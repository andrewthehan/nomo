package dev.andrewhan.nomo.example.poison

import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.isZero
import dev.andrewhan.nomo.sdk.util.min
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
open class PoisonSystem
@Inject
constructor(private val engine: NomoEngine, @Poison
private val dps: Float) :
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