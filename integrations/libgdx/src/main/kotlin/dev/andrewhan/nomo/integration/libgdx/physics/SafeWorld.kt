package dev.andrewhan.nomo.integration.libgdx.physics

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.DistanceJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.FrictionJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.GearJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.JointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.MotorJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.MouseJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.PrismaticJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.PulleyJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.RevoluteJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.RopeJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.WeldJointComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.WheelJointComponent
import dev.andrewhan.nomo.sdk.util.SafeAccessor
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.box2d.distanceJointWith
import ktx.box2d.frictionJointWith
import ktx.box2d.gearJointWith
import ktx.box2d.motorJointWith
import ktx.box2d.mouseJointWith
import ktx.box2d.prismaticJointWith
import ktx.box2d.pulleyJointWith
import ktx.box2d.revoluteJointWith
import ktx.box2d.ropeJointWith
import ktx.box2d.weldJointWith
import ktx.box2d.wheelJointWith

fun createSafeWorld(
  name: String,
  gravity: Vector2 = Vector2.Zero,
  allowSleep: Boolean = true
): SafeWorld = SafeWorld(name, createWorld(gravity, allowSleep))

class SafeWorld internal constructor(val name: String, world: World) : SafeAccessor<World>(world) {
  private val entityBodyMap: MutableMap<BodyComponent, Body> = mutableMapOf()
  private val entityJointMap: MutableMap<JointComponent<*>, Joint> = mutableMapOf()

  fun addBody(bodyComponent: BodyComponent) {
    assert(this == bodyComponent.world)
    safeRun { world ->
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
    return bodyComponent.world.safeRun { world ->
      if (!entityBodyMap.contains(bodyComponent)) {
        return@safeRun false
      }
      val body = bodyComponent.body
      world.destroyBody(body)
      body.component = null
      entityBodyMap.remove(bodyComponent, body)
    }
  }

  fun addJoint(jointComponent: JointComponent<*>) {
    assert(this == jointComponent.world)

    // jointWith uses world internally
    safeRun {
      if (entityJointMap.contains(jointComponent)) {
        return@safeRun
      }
      if (!entityBodyMap.contains(jointComponent.bodyA)) {
        addBody(jointComponent.bodyA)
      }
      if (!entityBodyMap.contains(jointComponent.bodyB)) {
        addBody(jointComponent.bodyB)
      }

      val joint =
        when (jointComponent) {
          is DistanceJointComponent ->
            jointComponent.bodyA.body.distanceJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is FrictionJointComponent ->
            jointComponent.bodyA.body.frictionJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is GearJointComponent ->
            jointComponent.bodyA.body.gearJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is MotorJointComponent ->
            jointComponent.bodyA.body.motorJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is MouseJointComponent ->
            jointComponent.bodyA.body.mouseJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is PrismaticJointComponent ->
            jointComponent.bodyA.body.prismaticJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is PulleyJointComponent ->
            jointComponent.bodyA.body.pulleyJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is RopeJointComponent ->
            jointComponent.bodyA.body.ropeJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is RevoluteJointComponent ->
            jointComponent.bodyA.body.revoluteJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is WeldJointComponent ->
            jointComponent.bodyA.body.weldJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
          is WheelJointComponent ->
            jointComponent.bodyA.body.wheelJointWith(
              jointComponent.bodyB.body,
              jointComponent.jointDef
            )
        }
      joint.component = jointComponent
      entityJointMap[jointComponent] = joint
    }
  }

  fun getJoint(jointComponent: JointComponent<*>): Joint {
    return entityJointMap[jointComponent]!!
  }

  fun removeJoint(jointComponent: JointComponent<*>): Boolean {
    return jointComponent.world.safeRun {
      if (!entityJointMap.contains(jointComponent)) {
        return@safeRun false
      }
      val joint = jointComponent.joint
      // could have been removed if the associated body was already removed
      if (it.allJoints.contains(joint)) {
        it.destroyJoint(joint)
      }
      joint.component = null
      entityJointMap.remove(jointComponent, joint)
    }
  }

  override fun toString(): String = "${this::class.simpleName}(name=$name)"
}
