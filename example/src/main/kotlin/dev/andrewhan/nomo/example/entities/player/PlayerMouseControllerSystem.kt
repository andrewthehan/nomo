package dev.andrewhan.nomo.example.entities.player

import com.badlogic.gdx.physics.box2d.BodyDef
import dev.andrewhan.nomo.boot.time.components.DelayedActionComponent
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.RopeJointComponent
import dev.andrewhan.nomo.integration.libgdx.render.components.getRelevantCamera
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.entities.entity
import dev.andrewhan.nomo.sdk.io.MouseButton
import dev.andrewhan.nomo.sdk.io.MouseButtonEvent
import dev.andrewhan.nomo.sdk.io.MouseHoldButtonEvent
import dev.andrewhan.nomo.sdk.io.MousePressButtonEvent
import dev.andrewhan.nomo.sdk.io.MouseReleaseButtonEvent
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import ktx.math.minus
import javax.inject.Inject
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.ExperimentalTime

class PlayerMouseControllerSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<MouseButtonEvent>() {
  private var node: Entity? = null

  @OptIn(ExperimentalTime::class)
  override suspend fun handle(event: MouseButtonEvent) {
    val player = engine.getEntityOrNull(PlayerComponent) ?: return
    val camera = engine.getRelevantCamera(event.location) ?: return
    when (event) {
      is MousePressButtonEvent -> {
        when (event.mouseButton) {
          MouseButton.LEFT -> {
            if (node != null) {
              engine.remove(node!!)
              node = null
            }

            val playerBody = engine.getComponent<BodyComponent>(player)
            node =
              engine.entity(
                BodyComponent(playerBody.world) {
                  type = BodyDef.BodyType.StaticBody
                  position.set(camera.toWorld(event.location))
                },
                DelayedActionComponent(1.nanoseconds) { entity, engine ->
                  val nodeBody = engine.getComponent<BodyComponent>(entity)
                  engine.entity(
                    RopeJointComponent(playerBody.world, playerBody, nodeBody) {
                      maxLength = (nodeBody.body.position - playerBody.body.position).len()
                    }
                  )
                }
              )
          }
          else -> {}
        }
      }
      is MouseHoldButtonEvent -> {}
      is MouseReleaseButtonEvent -> {
        node ?: return
        engine.remove(node!!)
        node = null
      }
    }
  }
}
