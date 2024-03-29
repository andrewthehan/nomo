package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
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
    engine
      .getComponents<BodyComponent>()
      .map { it.world }
      .distinct()
      .forEach { world ->
        @OptIn(ExperimentalTime::class)
        world.safeRun { it.step(event.elapsed.toFloat(DurationUnit.SECONDS), 6, 2) }
      }
  }
}
