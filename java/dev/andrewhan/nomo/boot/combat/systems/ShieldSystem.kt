package dev.andrewhan.nomo.boot.combat.systems

import dev.andrewhan.nomo.boot.combat.components.ShieldComponent
import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.systems.SystemFeatures
import javax.inject.Inject

fun ShieldComponent.absorb(event: DamageEvent) {
  event.amount = absorb(event.amount)
}

class ShieldSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<DamageEvent>(SystemFeatures.SWALLOW_EVENTS) {
  override suspend fun handle(event: DamageEvent) {
    val shield = engine.getComponentOrNull<ShieldComponent>(event.entity)

    if (shield == null) {
      emit(event)
      return
    }

    shield.absorb(event)
    if (shield.isDepleted) {
      engine.remove(shield)
    }

    if (event.hasDamage) {
      emit(event)
    }
  }
}
