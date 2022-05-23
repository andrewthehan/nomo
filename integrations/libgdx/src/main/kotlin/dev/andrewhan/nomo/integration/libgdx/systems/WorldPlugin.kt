package dev.andrewhan.nomo.integration.libgdx.systems

import dev.andrewhan.nomo.boot.physics.events.Force2dEvent
import dev.andrewhan.nomo.integration.libgdx.events.RenderEvent
import dev.andrewhan.nomo.sdk.engines.EnginePlugin
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import kotlinx.coroutines.CoroutineScope

@Suppress("FunctionName")
fun WorldPlugin(renderScope: CoroutineScope): EnginePlugin = {
  forEvent<UpdateEvent> { run<WorldStepSystem>() }
  forEvent<RenderEvent>(renderScope) { run<ClearRenderSystem>() then run<WorldRenderSystem>() }

  forEvent<Force2dEvent> { run<ForceApplicationSystem>() }
}
