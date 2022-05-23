package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.WorldBodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.WorldComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class WorldStepSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  override suspend fun handle(event: UpdateEvent) {
    engine.getComponents<WorldBodyComponent>().forEach { it.world.addBody(it) }
    engine.getComponents<WorldComponent>().forEach { it.world.cleanBodies(engine) }

    engine
      .getComponents<WorldComponent>()
      .map { it.world }
      .forEach { world ->
        @OptIn(ExperimentalTime::class)
        world.safeRun { it.step(event.elapsed.toFloat(DurationUnit.SECONDS), 6, 2) }
      }
  }
}
