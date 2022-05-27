package dev.andrewhan.nomo.integration.libgdx.physics.components

import com.badlogic.gdx.physics.box2d.JointDef
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef
import com.badlogic.gdx.physics.box2d.joints.GearJointDef
import com.badlogic.gdx.physics.box2d.joints.MotorJointDef
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant

sealed class JointComponent<JointType : JointDef>(
  val world: SafeWorld,
  val bodyA: BodyComponent,
  val bodyB: BodyComponent,
  val jointDef: JointType.() -> Unit
) : Component, Exclusive, Pendant {
  val joint
    get() = world.getJoint(this)

  override fun toString(): String =
    "${this::class.simpleName}(world=$world,bodyA=$bodyA,bodyB=$bodyB)"
}

class GearJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: GearJointDef.() -> Unit
) : JointComponent<GearJointDef>(world, bodyA, bodyB, jointDef)

class RopeJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: RopeJointDef.() -> Unit
) : JointComponent<RopeJointDef>(world, bodyA, bodyB, jointDef)

class WeldJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: WeldJointDef.() -> Unit
) : JointComponent<WeldJointDef>(world, bodyA, bodyB, jointDef)

class MotorJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: MotorJointDef.() -> Unit
) : JointComponent<MotorJointDef>(world, bodyA, bodyB, jointDef)

class MouseJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: MouseJointDef.() -> Unit
) : JointComponent<MouseJointDef>(world, bodyA, bodyB, jointDef)

class WheelJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: WheelJointDef.() -> Unit
) : JointComponent<WheelJointDef>(world, bodyA, bodyB, jointDef)

class PulleyJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: PulleyJointDef.() -> Unit
) : JointComponent<PulleyJointDef>(world, bodyA, bodyB, jointDef)

class DistanceJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: DistanceJointDef.() -> Unit
) : JointComponent<DistanceJointDef>(world, bodyA, bodyB, jointDef)

class FrictionJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: FrictionJointDef.() -> Unit
) : JointComponent<FrictionJointDef>(world, bodyA, bodyB, jointDef)

class RevoluteJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: RevoluteJointDef.() -> Unit
) : JointComponent<RevoluteJointDef>(world, bodyA, bodyB, jointDef)

class PrismaticJointComponent(
  world: SafeWorld,
  bodyA: BodyComponent,
  bodyB: BodyComponent,
  jointDef: PrismaticJointDef.() -> Unit
) : JointComponent<PrismaticJointDef>(world, bodyA, bodyB, jointDef)
