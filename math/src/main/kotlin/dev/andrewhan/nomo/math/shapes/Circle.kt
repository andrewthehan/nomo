package dev.andrewhan.nomo.math.shapes

import dev.andrewhan.nomo.math.vectors.Vector2f

data class Circle(override val center: Vector2f, override val radius: Float) :
  RegularPolygon(center, radius, 100)
