package dev.andrewhan.nomo.boot.combat.systems

import dev.andrewhan.nomo.boot.combat.components.HealthComponent
import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.boot.combat.events.DeathEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class DamageSystem @Inject constructor(private val engine: NomoEngine) : NomoSystem<DamageEvent>() {
  override suspend fun handle(event: DamageEvent) {
    engine.getComponentOrNull<HealthComponent>(event.entity)?.let {
      it.damage(event.amount)

      if (it.isDead) {
        engine.dispatchEvent(DeathEvent(event.entity))
      }
    }
  }
}
