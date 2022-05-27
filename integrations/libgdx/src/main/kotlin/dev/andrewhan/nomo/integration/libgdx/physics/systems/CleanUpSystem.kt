package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.JointComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getAssignableComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class CleanUpSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  override suspend fun handle(event: UpdateEvent) {
    engine
      .getAssignableComponents<JointComponent<*>>()
      .filter { !engine.contains(it.bodyA) || !engine.contains(it.bodyB) }
      .forEach { engine.remove(it) }
  }
}
