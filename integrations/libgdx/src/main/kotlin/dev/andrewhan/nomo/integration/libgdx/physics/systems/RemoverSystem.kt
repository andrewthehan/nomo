package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.JointComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.ComponentRemovedEvent
import dev.andrewhan.nomo.sdk.stores.getAssignableComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class RemoverSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<ComponentRemovedEvent>() {
  override suspend fun handle(event: ComponentRemovedEvent) {
    when (val component = event.component) {
      is BodyComponent -> {
        synchronized(component) {
          engine
            .getAssignableComponents<JointComponent<*>>()
            .filter { it.bodyA == component.body || it.bodyB == component.body }
            .forEach {
              assert(engine.remove(it).isNotEmpty()) { "Expected world to contain the joint." }
            }
        }
        check(component.world.removeBody(component)) { "Expected world to contain the body." }
      }
      is JointComponent<*> -> {
        check(component.world.removeJoint(component)) { "Expected world to contain the joint." }
      }
    }
  }
}
