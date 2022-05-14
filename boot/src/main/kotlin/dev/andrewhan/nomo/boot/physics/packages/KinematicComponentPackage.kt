package dev.andrewhan.nomo.boot.physics.packages

import dev.andrewhan.nomo.boot.physics.components.Acceleration2dComponent
import dev.andrewhan.nomo.boot.physics.components.Acceleration3dComponent
import dev.andrewhan.nomo.boot.physics.components.AccelerationComponent
import dev.andrewhan.nomo.boot.physics.components.Position2dComponent
import dev.andrewhan.nomo.boot.physics.components.Position3dComponent
import dev.andrewhan.nomo.boot.physics.components.PositionComponent
import dev.andrewhan.nomo.boot.physics.components.Velocity2dComponent
import dev.andrewhan.nomo.boot.physics.components.Velocity3dComponent
import dev.andrewhan.nomo.boot.physics.components.VelocityComponent
import dev.andrewhan.nomo.sdk.components.ComponentPackage

fun kinematic2dComponentPackage(
  builder: Kinematic2dComponentPackageBuilder.() -> Unit
): ComponentPackage = Kinematic2dComponentPackageBuilder().apply(builder).build()

class Kinematic2dComponentPackageBuilder :
  KinematicComponentPackageBuilder<
    Position2dComponent, Velocity2dComponent, Acceleration2dComponent>() {
  override var position = Position2dComponent()
  override var velocity = Velocity2dComponent()
  override var acceleration = Acceleration2dComponent()
}

fun kinematic3dComponentPackage(
  builder: Kinematic3dComponentPackageBuilder.() -> Unit
): ComponentPackage = Kinematic3dComponentPackageBuilder().apply(builder).build()

class Kinematic3dComponentPackageBuilder :
  KinematicComponentPackageBuilder<
    Position3dComponent, Velocity3dComponent, Acceleration3dComponent>() {
  override var position = Position3dComponent()
  override var velocity = Velocity3dComponent()
  override var acceleration = Acceleration3dComponent()
}

abstract class KinematicComponentPackageBuilder<
  Position : PositionComponent, Velocity : VelocityComponent, Acceleration : AccelerationComponent
> {
  protected abstract var position: Position
  protected abstract var velocity: Velocity
  protected abstract var acceleration: Acceleration

  fun position(builder: Position.() -> Unit) {
    position.builder()
  }

  fun velocity(builder: Velocity.() -> Unit) {
    velocity.builder()
  }

  fun acceleration(builder: Acceleration.() -> Unit) {
    acceleration.builder()
  }

  fun build(): ComponentPackage = ComponentPackage(position, velocity, acceleration)
}
