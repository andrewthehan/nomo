package dev.andrewhan.nomo.example.ball

import com.badlogic.gdx.physics.box2d.WorldManifold
import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.example.ball.BallComponent
import dev.andrewhan.nomo.integration.libgdx.physics.Direction
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.systems.CollisionHandlerSystem
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.containsComponent
import dev.andrewhan.nomo.sdk.stores.getComponent
import ktx.math.plus
import ktx.math.times
import javax.inject.Inject
import kotlin.math.absoluteValue

class BallCollisionSystem @Inject constructor(private val engine: NomoEngine) :
  CollisionHandlerSystem(engine) {
  override fun isFirstEntity(entity: Entity): Boolean =
    engine.containsComponent<PlayerComponent>(entity)

  override fun isSecondEntity(entity: Entity): Boolean =
    engine.containsComponent<BallComponent>(entity)

  override suspend fun handle(
    firstEntity: Entity,
    secondEntity: Entity,
    worldManifold: WorldManifold
  ) {
    val playerBody = engine.getComponent<BodyComponent>(firstEntity)

    val contactPoint = worldManifold.points[0]
    val localContactPoint = playerBody.body.getLocalPoint(contactPoint)

    val playerHeight = 1f // playerBody.body.fixtureList.single().shape.radius
    val regularThreshold = 1 / 3f
    if (localContactPoint.y.absoluteValue < playerHeight * regularThreshold / 2) return

    val ballBody = engine.getComponent<BodyComponent>(secondEntity)
    ballBody.body.linearVelocity =
      (Direction.RIGHT + localContactPoint).nor() * ballBody.body.linearVelocity.len()
  }
}
