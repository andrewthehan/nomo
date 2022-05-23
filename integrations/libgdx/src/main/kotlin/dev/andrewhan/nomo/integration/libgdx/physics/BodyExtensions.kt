package dev.andrewhan.nomo.integration.libgdx.physics

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import dev.andrewhan.nomo.core.Entity

val World.allBodies: Set<Body>
  get() {
    synchronized(this) {
      val bodies: Array<Body> = Array()
      this.getBodies(bodies)
      return bodies.toSet()
    }
  }

var Body.entity: Entity
  get() = userData as Entity
  set(value) {
    userData = value
  }
