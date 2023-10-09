package dev.andrewhan.nomo.examples.breakout.items.xp

import com.badlogic.gdx.physics.box2d.WorldManifold
import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.boot.xp.events.ExperienceEvent
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.examples.breakout.ball.BallComponent
import dev.andrewhan.nomo.integration.libgdx.physics.systems.CollisionHandlerSystem
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.containsComponent
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntity
import javax.inject.Inject

class ExperienceItemCollisionSystem @Inject constructor(private val engine: NomoEngine) :
    CollisionHandlerSystem(engine) {
  override fun isFirstEntity(entity: Entity): Boolean =
      engine.containsComponent<BallComponent>(entity)

  override fun isSecondEntity(entity: Entity): Boolean =
      engine.containsComponent<ExperienceItemComponent>(entity)

  override suspend fun handle(
      firstEntity: Entity,
      secondEntity: Entity,
      worldManifold: WorldManifold
  ) {
    val experienceItem = engine.getComponent<ExperienceItemComponent>(secondEntity)
    val player = engine.getEntity(PlayerComponent)
    engine.dispatchEvent(ExperienceEvent(experienceItem.amount, player))

    engine.remove(secondEntity)
  }
}
