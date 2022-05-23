package dev.andrewhan.nomo.integration.libgdx.systems

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.components.WorldBodyComponent
import dev.andrewhan.nomo.integration.libgdx.components.WorldComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import com.badlogic.gdx.utils.Array as GdxArray

private val World.allBodies: Set<Body>
  get() {
    synchronized(this) {
      val bodies: GdxArray<Body> = GdxArray()
      this.getBodies(bodies)
      return bodies.toSet()
    }
  }

private var Body.entity: Entity
  get() = userData as Entity
  set(value) {
    userData = value
  }

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
