package dev.andrewhan.nomo.integration.libgdx.physics.systems

import com.badlogic.gdx.physics.box2d.WorldManifold
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.physics.component
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class CollisionHandlerSystem(private val engine: NomoEngine) :
  NomoSystem<StartCollisionEvent>() {
  private val mutex = Mutex()

  abstract fun isFirstEntity(entity: Entity): Boolean
  abstract fun isSecondEntity(entity: Entity): Boolean

  abstract suspend fun handle(
    firstEntity: Entity,
    secondEntity: Entity,
    worldManifold: WorldManifold
  )

  final override suspend fun handle(event: StartCollisionEvent) {
    mutex.withLock {
      val bodyA = event.contact.fixtureA.body.component ?: return
      val bodyB = event.contact.fixtureB.body.component ?: return
      val entityA = engine.getEntityOrNull(bodyA) ?: return
      val entityB = engine.getEntityOrNull(bodyB) ?: return
      val worldManifold = event.contact.worldManifold
      if (isFirstEntity(entityA) && isSecondEntity(entityB)) {
        handle(entityA, entityB, worldManifold)
      } else if (isFirstEntity(entityB) && isSecondEntity(entityA)) {
        handle(entityB, entityA, worldManifold)
      }
    }
  }
}
