package dev.andrewhan.nomo.integration.libgdx.physics.systems

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.integration.libgdx.physics.components.WorldComponent
import dev.andrewhan.nomo.integration.libgdx.physics.events.EndCollisionEvent
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.sdk.engines.EngineCoroutineScope
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.ComponentAddedEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class WorldCollisionSystem
@Inject
constructor(
  private val engine: NomoEngine,
  @EngineCoroutineScope private val scope: CoroutineScope
) : NomoSystem<ComponentAddedEvent>() {
  override suspend fun handle(event: ComponentAddedEvent) {
    when (val component = event.component) {
      is WorldComponent ->
        component.world.safeRun {
          it.setContactListener(
            object : ContactListener {
              private fun dispatch(event: Event) {
                scope.launch { engine.dispatchEvent(event) }
              }

              override fun beginContact(contact: Contact) {
                dispatch(StartCollisionEvent(component.world, contact))
              }

              override fun endContact(contact: Contact) {
                dispatch(EndCollisionEvent(component.world, contact))
              }

              override fun preSolve(contact: Contact, oldManifold: Manifold) {}

              override fun postSolve(contact: Contact, impulse: ContactImpulse) {}
            }
          )
        }
    }
  }
}
