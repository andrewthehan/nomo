package dev.andrewhan.nomo.integration.libgdx.physics.systems

import com.badlogic.gdx.physics.box2d.WorldManifold
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.physics.components.DestroyOtherOnCollisionComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.DestroySelfOnCollisionComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.containsComponent
import javax.inject.Inject

class DestroyOnCollisionSystem @Inject constructor(private val engine: NomoEngine) :
  CollisionHandlerSystem(engine) {
  private fun shouldDestroySelf(entity: Entity): Boolean =
    engine.containsComponent<DestroySelfOnCollisionComponent>(entity)

  private fun shouldDestroyOther(entity: Entity): Boolean =
    engine.containsComponent<DestroyOtherOnCollisionComponent>(entity)

  override fun isFirstEntity(entity: Entity): Boolean =
    shouldDestroySelf(entity) || shouldDestroyOther(entity)

  override fun isSecondEntity(entity: Entity): Boolean = true

  override suspend fun handle(
    firstEntity: Entity,
    secondEntity: Entity,
    worldManifold: WorldManifold
  ) {
    val shouldDestroyFirst = shouldDestroySelf(firstEntity) || shouldDestroyOther(secondEntity)
    val shouldDestroySecond = shouldDestroySelf(secondEntity) || shouldDestroyOther(firstEntity)
    if (shouldDestroyFirst) {
      engine.remove(firstEntity)
    }
    if (shouldDestroySecond) {
      engine.remove(secondEntity)
    }
  }
}
