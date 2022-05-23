package dev.andrewhan.nomo.integration.libgdx.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.sdk.components.Exclusive

class WorldComponent(val world: SafeWorld) : Component, Exclusive
