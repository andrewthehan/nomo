package dev.andrewhan.nomo.integration.libgdx.physics

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.sdk.util.SafeAccessor
import ktx.box2d.body
import ktx.box2d.createWorld

fun createSafeWorld(gravity: Vector2 = Vector2.Zero, allowSleep: Boolean = true): SafeWorld =
  SafeWorld(createWorld(gravity, allowSleep))

class SafeWorld internal constructor(world: World) : SafeAccessor<World>(world) {
  private val entityBodyMap: MutableMap<BodyComponent, Body> = mutableMapOf()

  fun addBody(bodyComponent: BodyComponent) {
    safeRun { world ->
      bodyComponent.world.safeRun { assert(world == it) }

      if (entityBodyMap.contains(bodyComponent)) {
        return@safeRun
      }

      val body = world.body(init = bodyComponent.bodyDef)
      body.component = bodyComponent
      entityBodyMap[bodyComponent] = body
    }
  }

  fun getBody(bodyComponent: BodyComponent): Body {
    return entityBodyMap[bodyComponent]!!
  }

  fun removeBody(bodyComponent: BodyComponent): Boolean {
    bodyComponent.world.safeRun { it.destroyBody(bodyComponent.body) }
    return entityBodyMap.remove(bodyComponent, bodyComponent.body)
  }
}
