package dev.andrewhan.nomo.integration.libgdx.render.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraComponent
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraFollowComponent
import dev.andrewhan.nomo.integration.libgdx.render.components.ExactCameraFollowComponent
import dev.andrewhan.nomo.integration.libgdx.render.components.InverseQuadraticCameraFollowComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getAssignableComponents
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.toFloat
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class CameraFollowBodySystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  @OptIn(ExperimentalTime::class)
  override suspend fun handle(event: UpdateEvent) {
    engine.getAssignableComponents<CameraFollowComponent>().forEach { cameraFollow ->
      val entity = engine.getEntity(cameraFollow)

      val camera = engine.getComponentOrNull<CameraComponent>(entity) ?: return@forEach
      val body = engine.getComponentOrNull<BodyComponent>(entity) ?: return@forEach

      val target = body.body?.position ?: return@forEach
      val distance = target - camera.worldCenter
      when (cameraFollow) {
        is ExactCameraFollowComponent -> camera.worldCenter = target
        is InverseQuadraticCameraFollowComponent ->
          camera.worldCenter +=
            distance * event.elapsed.toFloat(DurationUnit.SECONDS) * cameraFollow.speed
      }
    }
  }
}
