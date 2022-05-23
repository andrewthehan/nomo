package dev.andrewhan.nomo.integration.libgdx.physics.systems

import dev.andrewhan.nomo.integration.libgdx.physics.allBodies
import dev.andrewhan.nomo.integration.libgdx.physics.components.WorldBodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.WorldComponent
import dev.andrewhan.nomo.integration.libgdx.physics.entity
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class WorldStepSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  override suspend fun handle(event: UpdateEvent) {
    engine.getComponents<WorldBodyComponent>().forEach {
      engine.getEntityOrNull(it)?.apply { it.body.entity = this }
    }

    engine
      .getComponents<WorldComponent>()
      .map { it.world }
      .forEach { world ->
        world.allBodies
          .filter { !engine.contains(it.entity) }
          .forEach { synchronized(world) { world.destroyBody(it) } }

        @OptIn(ExperimentalTime::class)
        world.step(event.elapsed.toFloat(DurationUnit.SECONDS), 6, 2)
      }
  }
}
