package dev.andrewhan.nomo.boot.combat.systems

import dev.andrewhan.nomo.boot.combat.events.DeathEvent
import dev.andrewhan.nomo.sdk.BasicEngine
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class DeathSystem @Inject constructor(private val engine: BasicEngine) : NomoSystem<DeathEvent>() {
  override suspend fun handle(event: DeathEvent) {
    engine.remove(event.entity)
  }
}