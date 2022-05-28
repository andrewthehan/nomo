package dev.andrewhan.nomo.example.player

import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.example.ball.newBall
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.io.MouseButton
import dev.andrewhan.nomo.sdk.io.MouseButtonEvent
import dev.andrewhan.nomo.sdk.io.MouseHoldButtonEvent
import dev.andrewhan.nomo.sdk.io.MousePressButtonEvent
import dev.andrewhan.nomo.sdk.io.MouseReleaseButtonEvent
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.systems.SystemFeatures
import javax.inject.Inject

class PlayerMouseControllerSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<MouseButtonEvent>(SystemFeatures.CONCURRENT) {
  override suspend fun handle(event: MouseButtonEvent) {
    val player = engine.getEntityOrNull(PlayerComponent) ?: return
    when (event) {
      is MousePressButtonEvent -> {
        when (event.mouseButton) {
          MouseButton.LEFT -> {
//            engine.newBall(engine.getComponent<BodyComponent>(player).world)
          }
          else -> {}
        }
      }
      is MouseReleaseButtonEvent -> {}
      is MouseHoldButtonEvent -> {}
    }
  }
}
