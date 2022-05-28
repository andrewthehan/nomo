package dev.andrewhan.nomo.example.poison

import dev.andrewhan.nomo.integration.libgdx.physics.component
import dev.andrewhan.nomo.integration.libgdx.physics.events.StartCollisionEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponentOrNull
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.max
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class PoisonSpreaderSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<StartCollisionEvent>() {
  @OptIn(ExperimentalTime::class)
  override suspend fun handle(event: StartCollisionEvent) {
    val entityA = engine.getEntity(event.contact.fixtureA.body.component ?: return)
    val entityB = engine.getEntity(event.contact.fixtureB.body.component ?: return)

    val poisonA = engine.getComponentOrNull<PoisonComponent>(entityA)
    val poisonB = engine.getComponentOrNull<PoisonComponent>(entityB)

    if (poisonA == null && poisonB == null) {
      return
    }

    val maxDuration = max(poisonA?.duration ?: Duration.ZERO, poisonB?.duration ?: Duration.ZERO)

    if (poisonA == null) {
      engine.add(entityA, PoisonComponent(maxDuration))
    } else {
      poisonA.duration = maxDuration
    }

    if (poisonB == null) {
      engine.add(entityB, PoisonComponent(maxDuration))
    } else {
      poisonB.duration = maxDuration
    }
  }
}
