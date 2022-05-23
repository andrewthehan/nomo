package dev.andrewhan.nomo.integration.libgdx.physics

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.integration.libgdx.physics.components.WorldBodyComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.util.SafeAccessor
import ktx.box2d.body
import ktx.box2d.createWorld

fun createSafeWorld(gravity: Vector2 = Vector2.Zero, allowSleep: Boolean = true): SafeWorld =
  SafeWorld(createWorld(gravity, allowSleep))

class SafeWorld internal constructor(world: World) : SafeAccessor<World>(world) {
  private val entityBodyMap: MutableMap<WorldBodyComponent, Body> = mutableMapOf()

  fun addBody(worldBodyComponent: WorldBodyComponent) {
    safeRun { world ->
      worldBodyComponent.world.safeRun { assert(world == it) }

      if (entityBodyMap.contains(worldBodyComponent)) {
        return@safeRun
      }

      val body = world.body(init = worldBodyComponent.bodyDef)
      body.component = worldBodyComponent
      entityBodyMap[worldBodyComponent] = body
    }
  }

  fun getBody(worldBodyComponent: WorldBodyComponent): Body {
    return entityBodyMap[worldBodyComponent]!!
  }

  fun cleanBodies(engine: NomoEngine) {
    entityBodyMap
      .filter { (worldBodyComponent, body) -> !engine.contains(worldBodyComponent) }
      .forEach { (worldBodyComponent, body) ->
        entityBodyMap.remove(worldBodyComponent, body)
        worldBodyComponent.world.safeRun { it.destroyBody(body) }
      }
  }
}
