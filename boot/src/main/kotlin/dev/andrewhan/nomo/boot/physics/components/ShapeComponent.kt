package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.math.shapes.Shape
import dev.andrewhan.nomo.sdk.interfaces.Exclusive
import dev.andrewhan.nomo.sdk.interfaces.Pendant

data class ShapeComponent(val shape: Shape) : Component, Pendant, Exclusive
