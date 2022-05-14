package dev.andrewhan.nomo.math.shapes

import dev.andrewhan.nomo.math.vectors.Vector2f

open class Circle : RegularPolygon {
  constructor(center: Vector2f, radius: Float) : super(center, radius, 100)
}
