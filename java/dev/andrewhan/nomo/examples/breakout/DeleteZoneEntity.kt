package dev.andrewhan.nomo.examples.breakout

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.DestroyOtherOnCollisionComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.entities.entity
import dev.andrewhan.nomo.sdk.util.Size
import ktx.box2d.box

fun NomoEngine.newDeleteZone(world: SafeWorld, center: Vector2, size: Size): Entity =
    entity(
        DestroyOtherOnCollisionComponent,
        BodyComponent(world) {
          type = BodyDef.BodyType.StaticBody
          position.set(center)
          fixedRotation = true
          box(width = size.width, height = size.height) { isSensor = true }
        }
    )
