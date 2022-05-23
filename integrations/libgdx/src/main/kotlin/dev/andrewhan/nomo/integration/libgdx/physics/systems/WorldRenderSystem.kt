package dev.andrewhan.nomo.integration.libgdx.physics.systems

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import dev.andrewhan.nomo.integration.libgdx.physics.components.WorldComponent
import dev.andrewhan.nomo.integration.libgdx.render.events.RenderEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class WorldRenderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<RenderEvent>() {
  private val debugRenderer by lazy { Box2DDebugRenderer() }

  override suspend fun handle(event: RenderEvent) {
    engine.getComponents<WorldComponent>().forEach {
      synchronized(it.world) { debugRenderer.render(it.world, event.camera.combined) }
    }
  }
}
