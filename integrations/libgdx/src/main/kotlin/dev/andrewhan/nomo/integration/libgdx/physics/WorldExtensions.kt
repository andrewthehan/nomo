package dev.andrewhan.nomo.integration.libgdx.physics

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent

val World.allBodies: Set<Body>
  get() {
    val bodies: Array<Body> = Array()
    this.getBodies(bodies)
    return bodies.toSet()
  }

var Body.component: BodyComponent
  get() = userData as BodyComponent
  set(value) {
    userData = value
  }

// val Body.entityOrNull: Entity?
//  get() = userData as Entity?
