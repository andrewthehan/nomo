package dev.andrewhan.nomo.integration.libgdx.systems

import dev.andrewhan.nomo.boot.physics.events.Force2dEvent
import dev.andrewhan.nomo.integration.libgdx.components.WorldBodyComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class ForceApplicationSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<Force2dEvent>() {
  override suspend fun handle(event: Force2dEvent) {
    engine
      .getComponent<WorldBodyComponent>(event.entity)
      .body.applyForceToCenter(event.newtons.x, event.newtons.y, true)
  }
}
