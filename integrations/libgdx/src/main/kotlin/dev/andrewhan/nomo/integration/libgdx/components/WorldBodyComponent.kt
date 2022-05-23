package dev.andrewhan.nomo.integration.libgdx.components

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant

data class WorldBodyComponent(val world: World, val body: Body) : Component, Exclusive, Pendant
