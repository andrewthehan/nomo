package dev.andrewhan.nomo.example

import dev.andrewhan.nomo.example.ball.BallComponent
import dev.andrewhan.nomo.example.player.PlayerComponent
import dev.andrewhan.nomo.integration.libgdx.physics.Direction
import dev.andrewhan.nomo.integration.libgdx.physics.component
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import ktx.math.plus
import ktx.math.times
import javax.inject.Inject
import kotlin.math.absoluteValue

class BallCollisionSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<StartCollisionEvent>() {
  override suspend fun handle(event: StartCollisionEvent) {
    val ball = engine.getEntity(BallComponent)

    val entityA = engine.getEntity(event.contact.fixtureA.body.component)
    val entityB = engine.getEntity(event.contact.fixtureB.body.component)

    if (entityA != ball && entityB != ball) return

    val other = if (ball == entityA) entityB else entityA

    engine.getComponentOrNull<PlayerComponent>(other) ?: return

    val playerBody = engine.getComponent<BodyComponent>(other)

    val contactPoint = event.contact.worldManifold.points[0]
    val localContactPoint = playerBody.body.getLocalPoint(contactPoint)

    val playerHeight = 1f // playerBody.body.fixtureList.single().shape.radius
    val regularThreshold = 0.5f
    if (localContactPoint.y.absoluteValue < playerHeight / 2 * regularThreshold) return

    val ballBody = engine.getComponent<BodyComponent>(ball)
    ballBody.body.linearVelocity =
      (Direction.RIGHT + localContactPoint).nor() * ballBody.body.linearVelocity.len()
  }
}
