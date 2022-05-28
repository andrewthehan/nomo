package dev.andrewhan.nomo.integration.libgdx.physics.systems

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.events.EndCollisionEvent
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.sdk.engines.EngineCoroutineScope
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.ComponentAddedEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImmutableContact(world: World, addr: Long) : Contact(world, addr) {
  init {
    getWorldManifold()
  }
}

class CollisionSystem
@Inject
constructor(
  private val engine: NomoEngine,
  @EngineCoroutineScope private val scope: CoroutineScope
) : NomoSystem<ComponentAddedEvent>() {
  private val worldContactListenerMap: MutableMap<SafeWorld, ContactListener> = mutableMapOf()

  private fun addContactListener(world: SafeWorld) {
    val contactListener =
      object : ContactListener {
        private fun Contact.copy(): Contact {
          val worldField = Contact::class.java.getDeclaredField("world")
          worldField.isAccessible = true

          val addrField = Contact::class.java.getDeclaredField("addr")
          addrField.isAccessible = true

          return ImmutableContact(worldField.get(this) as World, addrField.get(this) as Long)
        }

        private fun dispatch(event: Event) {
          scope.launch { engine.dispatchEvent(event) }
        }

        override fun beginContact(contact: Contact) {
          dispatch(StartCollisionEvent(world, contact.copy()))
        }

        override fun endContact(contact: Contact) {
          dispatch(EndCollisionEvent(world, contact.copy()))
        }

        override fun preSolve(contact: Contact, oldManifold: Manifold) {}

        override fun postSolve(contact: Contact, impulse: ContactImpulse) {}
      }
    world.safeRun {
      it.setContactListener(contactListener)
      worldContactListenerMap[world] = contactListener
    }
  }

  override suspend fun handle(event: ComponentAddedEvent) {
    when (val component = event.component) {
      is BodyComponent -> {
        if (worldContactListenerMap.contains(component.world)) {
          return
        }
        addContactListener(component.world)
      }
    }
  }
}
