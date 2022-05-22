package dev.andrewhan.nomo.boot.physics.systems

import dev.andrewhan.nomo.boot.physics.components.Acceleration2dComponent
import dev.andrewhan.nomo.boot.physics.components.Acceleration3dComponent
import dev.andrewhan.nomo.boot.physics.components.AccelerationComponent
import dev.andrewhan.nomo.boot.physics.components.DynamicBodyComponent
import dev.andrewhan.nomo.boot.physics.components.Kinetic2dComponent
import dev.andrewhan.nomo.boot.physics.components.Kinetic3dComponent
import dev.andrewhan.nomo.boot.physics.components.KineticComponent
import dev.andrewhan.nomo.boot.physics.components.MassComponent
import dev.andrewhan.nomo.boot.physics.components.Position2dComponent
import dev.andrewhan.nomo.boot.physics.components.Position3dComponent
import dev.andrewhan.nomo.boot.physics.components.Velocity2dComponent
import dev.andrewhan.nomo.boot.physics.components.Velocity3dComponent
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.math.vectors.MutableVector
import dev.andrewhan.nomo.math.vectors.Vector
import dev.andrewhan.nomo.math.vectors.div
import dev.andrewhan.nomo.math.vectors.isZero
import dev.andrewhan.nomo.math.vectors.plusAssign
import dev.andrewhan.nomo.math.vectors.set
import dev.andrewhan.nomo.math.vectors.times
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.toFloat
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

abstract class PhysicsStepSystem(protected val engine: NomoEngine) : NomoSystem<UpdateEvent>() {
  protected inline fun <reified AccelerationType, reified KineticType> computeAcceleration() where
  AccelerationType : AccelerationComponent,
  AccelerationType : MutableVector<Float>,
  KineticType : KineticComponent<out Vector<Float>> {
    engine
      .getComponents<DynamicBodyComponent>()
      .flatMap { engine[it] }
      .forEach {
        val acceleration = engine.getComponent<AccelerationType>(it)
        val kinetic = engine.getComponent<KineticType>(it)
        val mass = engine.getComponent<MassComponent>(it)

        acceleration.set(kinetic.netForce / mass.amount)
        kinetic.reset()
      }
  }

  protected inline fun <reified A, reified B> step(delta: Float) where
  A : MutableVector<Float>,
  A : Component,
  A : Exclusive,
  B : MutableVector<Float>,
  B : Component,
  B : Exclusive {
    engine
      .getComponents<A>()
      .filter { !it.isZero }
      .forEach { a ->
        val entities = engine[a]
        entities.forEach { entity ->
          val b = engine.getComponent<B>(entity)
          b += a * delta
        }
      }
  }
}

@ExperimentalTime
class Physics2dStepSystem @Inject constructor(engine: NomoEngine) : PhysicsStepSystem(engine) {
  override suspend fun handle(event: UpdateEvent) {
    computeAcceleration<Acceleration2dComponent, Kinetic2dComponent>()
    step<Velocity2dComponent, Position2dComponent>(event.elapsed.toFloat(DurationUnit.SECONDS))
    step<Acceleration2dComponent, Velocity2dComponent>(event.elapsed.toFloat(DurationUnit.SECONDS))
  }
}

@ExperimentalTime
class Physics3dStepSystem @Inject constructor(engine: NomoEngine) : PhysicsStepSystem(engine) {
  override suspend fun handle(event: UpdateEvent) {
    computeAcceleration<Acceleration3dComponent, Kinetic3dComponent>()
    step<Velocity3dComponent, Position3dComponent>(event.elapsed.toFloat(DurationUnit.SECONDS))
    step<Acceleration3dComponent, Velocity3dComponent>(event.elapsed.toFloat(DurationUnit.SECONDS))
  }
}
