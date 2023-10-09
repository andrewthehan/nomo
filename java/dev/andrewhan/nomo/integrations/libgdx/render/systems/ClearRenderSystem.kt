package dev.andrewhan.nomo.integration.libgdx.render.systems

import dev.andrewhan.nomo.integration.libgdx.render.events.RenderEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import ktx.app.clearScreen

class ClearRenderSystem : NomoSystem<RenderEvent>() {
  override suspend fun handle(event: RenderEvent) {
    clearScreen(0f, 0f, 0f, 1f)
  }
}
