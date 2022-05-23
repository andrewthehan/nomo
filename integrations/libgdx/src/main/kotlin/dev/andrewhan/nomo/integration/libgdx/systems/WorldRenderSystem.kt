package dev.andrewhan.nomo.integration.libgdx.systems

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import dev.andrewhan.nomo.integration.libgdx.components.WorldComponent
import dev.andrewhan.nomo.integration.libgdx.events.RenderEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class WorldRenderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<RenderEvent>() {
  private val debugRenderer by lazy { Box2DDebugRenderer() }

  override suspend fun handle(event: RenderEvent) {
    engine.getComponents<WorldComponent>().forEach {
      debugRenderer.render(it.world, event.camera.combined)
    }
  }
}
