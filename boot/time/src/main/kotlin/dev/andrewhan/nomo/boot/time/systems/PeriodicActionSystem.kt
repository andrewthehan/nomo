package dev.andrewhan.nomo.boot.time.systems

import dev.andrewhan.nomo.boot.time.components.PeriodicActionComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class PeriodicActionSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  @OptIn(ExperimentalTime::class)
  override suspend fun handle(event: UpdateEvent) {
    engine.getComponents<PeriodicActionComponent>().forEach { it.update(event.elapsed, engine) }
  }
}
