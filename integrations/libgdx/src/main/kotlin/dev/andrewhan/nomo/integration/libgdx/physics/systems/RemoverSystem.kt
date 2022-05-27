package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.JointComponent
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem

class RemoverSystem : NomoSystem<ComponentRemovedEvent>() {
  override suspend fun handle(event: ComponentRemovedEvent) {
    when (val component = event.component) {
      is BodyComponent -> {
        check(component.world.removeBody(component)) { "Expected world to contain the body." }
      }
      is JointComponent<*> -> {
        check(component.world.removeJoint(component)) { "Expected world to contain the joint." }
      }
    }
  }
}
