package dev.andrewhan.nomo.integration.libgdx.physics.systems

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraComponent
import dev.andrewhan.nomo.integration.libgdx.render.events.RenderEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class BodyRenderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<RenderEvent>() {
  private val debugRenderer by lazy { Box2DDebugRenderer() }

  override suspend fun handle(event: RenderEvent) {
    val cameras = engine.getComponents<CameraComponent>()
    val worlds = engine.getComponents<BodyComponent>().map { it.world }.distinct()

    cameras.forEach { cameraComponent ->
      worlds.forEach { safeWorld ->
        safeWorld.safeRun { world ->
          cameraComponent.use { camera -> debugRenderer.render(world, camera.combined) }
        }
      }
    }
  }
}
