package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.WorldBodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.events.ForceEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class ForceApplicationSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<ForceEvent>() {
  override suspend fun handle(event: ForceEvent) {
    val body = engine.getComponent<WorldBodyComponent>(event.entity).body
    body.applyForceToCenter(event.newtons, true)
  }
}
