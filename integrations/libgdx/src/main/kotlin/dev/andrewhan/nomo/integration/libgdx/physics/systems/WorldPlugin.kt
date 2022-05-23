package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.events.ForceEvent
import dev.andrewhan.nomo.integration.libgdx.render.events.RenderEvent
import dev.andrewhan.nomo.integration.libgdx.render.systems.ClearRenderSystem
import dev.andrewhan.nomo.sdk.engines.EnginePlugin
import dev.andrewhan.nomo.sdk.events.ComponentAddedEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import kotlinx.coroutines.CoroutineScope

@Suppress("FunctionName")
fun WorldPlugin(renderScope: CoroutineScope): EnginePlugin = {
  forEvent<UpdateEvent> { run<WorldStepSystem>() }
  forEvent<RenderEvent>(renderScope) { run<ClearRenderSystem>() then run<WorldRenderSystem>() }
  forEvent<ComponentAddedEvent> { run<WorldCollisionSystem>() }

  forEvent<ForceEvent> { run<ForceApplicationSystem>() }
}
