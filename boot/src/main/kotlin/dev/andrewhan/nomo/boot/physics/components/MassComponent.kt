package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.interfaces.Exclusive

data class MassComponent(val amount: Float) : Component, Exclusive
