package dev.andrewhan.nomo.integration.libgdx.systems

import dev.andrewhan.nomo.integration.libgdx.events.LibgdxRenderEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import ktx.app.clearScreen

class ClearRenderSystem : NomoSystem<LibgdxRenderEvent>() {
  override suspend fun handle(event: LibgdxRenderEvent) {
    clearScreen(0f, 0f, 0f, 1f)
  }
}
