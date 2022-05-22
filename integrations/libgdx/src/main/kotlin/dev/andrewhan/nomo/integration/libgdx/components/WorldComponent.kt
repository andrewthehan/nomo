package dev.andrewhan.nomo.integration.libgdx.components

import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive

data class WorldComponent(val world: World) : Component, Exclusive
