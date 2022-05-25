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
  private val bodyComponentA: BodyComponent,
  private val bodyComponentB: BodyComponent,
  val jointDef: JointType.() -> Unit
) : Component, Exclusive, Pendant {
  val joint
    get() = world.getJoint(this)

  val bodyA
    get() = bodyComponentA.body
  val bodyB
    get() = bodyComponentB.body
}

class GearJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: GearJointDef.() -> Unit
) : JointComponent<GearJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class RopeJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: RopeJointDef.() -> Unit
) : JointComponent<RopeJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class WeldJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: WeldJointDef.() -> Unit
) : JointComponent<WeldJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class MotorJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: MotorJointDef.() -> Unit
) : JointComponent<MotorJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class MouseJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: MouseJointDef.() -> Unit
) : JointComponent<MouseJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class WheelJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: WheelJointDef.() -> Unit
) : JointComponent<WheelJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class PulleyJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: PulleyJointDef.() -> Unit
) : JointComponent<PulleyJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class DistanceJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: DistanceJointDef.() -> Unit
) : JointComponent<DistanceJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class FrictionJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: FrictionJointDef.() -> Unit
) : JointComponent<FrictionJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class RevoluteJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: RevoluteJointDef.() -> Unit
) : JointComponent<RevoluteJointDef>(world, bodyComponentA, bodyComponentB, jointDef)

class PrismaticJointComponent(
  world: SafeWorld,
  bodyComponentA: BodyComponent,
  bodyComponentB: BodyComponent,
  jointDef: PrismaticJointDef.() -> Unit
) : JointComponent<PrismaticJointDef>(world, bodyComponentA, bodyComponentB, jointDef)
