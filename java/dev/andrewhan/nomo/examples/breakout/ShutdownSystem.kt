package dev.andrewhan.nomo.examples.breakout

import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject
import kotlin.system.exitProcess

class ShutdownSystem @Inject constructor(private val engine: NomoEngine) :
    NomoSystem<ComponentRemovedEvent>() {
  override suspend fun handle(event: ComponentRemovedEvent) {
    if (engine.entities.isEmpty()) {
      exitProcess(0)
    }
  }
}
