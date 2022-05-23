package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive

data class MassComponent(val mass: Float) : Component, Exclusive
