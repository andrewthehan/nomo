package dev.andrewhan.nomo.boot.collision.detectors

import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.sdk.stores.EntityComponentStore

interface CollisionDetector {
  fun isColliding(
    entityComponentStore: EntityComponentStore,
    entityA: Entity,
    entityB: Entity
  ): Boolean
}
