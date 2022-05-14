package dev.andrewhan.nomo.boot.combat.systems

import dev.andrewhan.nomo.boot.combat.components.ArmorComponent
import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

fun ArmorComponent.block(event: DamageEvent) {
  event.amount = block(event.amount)
}

class ArmorSystem @Inject constructor(private val engine: NomoEngine) : NomoSystem<DamageEvent>() {
  override suspend fun handle(event: DamageEvent) {
    engine.getComponentOrNull<ArmorComponent>(event.entity)?.apply { block(event) }
  }
}
