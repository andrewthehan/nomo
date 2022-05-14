package dev.andrewhan.nomo.boot.collision.systems

import dev.andrewhan.nomo.boot.collision.components.CollidableComponent
import dev.andrewhan.nomo.boot.collision.detectors.CollisionDetector
import dev.andrewhan.nomo.boot.collision.events.CollisionEvent
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.sdk.BasicEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getEntities
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class CollisionDetectionSystem
@Inject
constructor(
  private val engine: BasicEngine,
  private val collisionDetectors: Set<CollisionDetector>
) : NomoSystem<UpdateEvent>() {
  private fun isColliding(entityA: Entity, entityB: Entity): Boolean {
    return collisionDetectors.isNotEmpty() &&
      collisionDetectors.all { it.isColliding(engine, entityA, entityB) }
  }

  override suspend fun handle(event: UpdateEvent) {
    val entities = engine.getEntities<CollidableComponent>()
    entities.forEach { entityA ->
      entities
        .filter { it !== entityA }
        .forEach { entityB ->
          if (isColliding(entityA, entityB)) {
            engine.dispatchEvent(CollisionEvent(entityA, entityB))
          }
        }
    }
  }
}
