package dev.andrewhan.nomo.examples.breakout.ball

import com.badlogic.gdx.physics.box2d.WorldManifold
import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.examples.breakout.player.PlayerStatsComponent
import dev.andrewhan.nomo.integration.libgdx.physics.Direction
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.systems.CollisionHandlerSystem
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.containsComponent
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntity
import javax.inject.Inject
import kotlin.math.absoluteValue
import ktx.math.plus
import ktx.math.times

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
                val player = engine.getEntity(PlayerComponent)
                val stats = engine.getComponent<PlayerStatsComponent>(player)

                val playerBody = engine.getComponent<BodyComponent>(firstEntity).body ?: return

                val contactPoint = worldManifold.points[0]
                val localContactPoint = playerBody.getLocalPoint(contactPoint)

                // hitting the back of the player
                if (localContactPoint.x < 0) return

                val playerHeight = stats.size
                val regularThreshold = 1 / 3f
                if (localContactPoint.y.absoluteValue < playerHeight * regularThreshold / 2) return

                val ballBody = engine.getComponent<BodyComponent>(secondEntity).body ?: return
                ballBody.linearVelocity =
                                (Direction.RIGHT + localContactPoint).nor() *
                                                ballBody.linearVelocity.len()
        }
}
