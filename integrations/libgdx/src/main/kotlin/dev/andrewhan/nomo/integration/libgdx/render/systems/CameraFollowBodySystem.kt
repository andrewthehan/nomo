package dev.andrewhan.nomo.integration.libgdx.render.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import ktx.math.minus
import ktx.math.plus
import ktx.math.times

class CameraFollowBodySystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  @OptIn(ExperimentalTime::class)
  override suspend fun handle(event: UpdateEvent) {
    engine.getComponents<CameraComponent>().forEach { camera ->
      val body = engine.getComponentOrNull<BodyComponent>(engine.getEntity(camera))
      if (body != null) {
        val distance = body.body.position - camera.worldCenter
        camera.worldCenter += distance * event.elapsed.toFloat(DurationUnit.SECONDS) * 4f
      }
    }
  }
}
