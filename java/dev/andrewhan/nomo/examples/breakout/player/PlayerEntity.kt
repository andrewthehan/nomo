package dev.andrewhan.nomo.examples.breakout.player

import com.badlogic.gdx.physics.box2d.BodyDef
import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.sdk.components.ComponentPackage
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.entities.entity
import ktx.box2d.box

fun NomoEngine.newPlayer(world: SafeWorld): Entity =
    entity(
        ComponentPackage(
            PlayerComponent,
            PlayerLevelComponent(),
            PlayerStatsComponent(),
            BodyComponent(world) {
              type = BodyDef.BodyType.KinematicBody
              position.set(-5f, 0f)
              fixedRotation = true
              box(width = 0.1f, height = 1f) {
                friction = 0f
                restitution = 1f
              }
            }
        )
    )
