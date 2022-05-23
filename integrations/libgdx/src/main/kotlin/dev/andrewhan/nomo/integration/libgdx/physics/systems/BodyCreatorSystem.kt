package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.sdk.events.ComponentAddedEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem

class BodyCreatorSystem : NomoSystem<ComponentAddedEvent>() {
  override suspend fun handle(event: ComponentAddedEvent) {
    when (val component = event.component) {
      is BodyComponent -> component.world.addBody(component)
    }
  }
}
