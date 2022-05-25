package dev.andrewhan.nomo.example.entities.player

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import dev.andrewhan.nomo.boot.time.components.DelayedActionComponent
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.RopeJointComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.entities.entity
import dev.andrewhan.nomo.sdk.io.MouseButtonEvent
import dev.andrewhan.nomo.sdk.io.MouseHoldButtonEvent
import dev.andrewhan.nomo.sdk.io.MousePressButtonEvent
import dev.andrewhan.nomo.sdk.io.MouseReleaseButtonEvent
import dev.andrewhan.nomo.sdk.io.MouseButton
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import ktx.math.minus

class PlayerMouseControllerSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<MouseButtonEvent>() {
  private var node: Entity? = null

  @OptIn(ExperimentalTime::class)
  override suspend fun handle(event: MouseButtonEvent) {
    val player = engine.getEntityOrNull(PlayerComponent) ?: return
    when (event) {
      is MousePressButtonEvent -> {
        when (event.mouseButton) {
          MouseButton.LEFT -> {
            val playerBody = engine.getComponent<BodyComponent>(player)
            node =
              engine.entity(
                BodyComponent(playerBody.world) {
                  type = BodyDef.BodyType.StaticBody
                  // TODO: hack to convert screen coordinates to world coordinates
                  position.set(Vector2(event.location.x / 100f, event.location.y / 100f))
                },
                DelayedActionComponent { entity, engine ->
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
