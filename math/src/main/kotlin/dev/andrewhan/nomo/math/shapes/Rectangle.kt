package dev.andrewhan.nomo.math.shapes

import dev.andrewhan.nomo.math.vectors.Vector2f
import dev.andrewhan.nomo.math.vectors.plus
import dev.andrewhan.nomo.math.vectors.vectorOf

open class Rectangle : Shape {
  override val points: List<Vector2f>
  override val center: Vector2f

  constructor(center: Vector2f, width: Float, height: Float) {
    val halfWidth = width / 2
    val halfHeight = height / 2
    this.points =
      listOf(
        center + vectorOf(-halfWidth, -halfHeight),
        center + vectorOf(halfWidth, -halfHeight),
        center + vectorOf(halfWidth, halfHeight),
        center + vectorOf(-halfWidth, halfHeight)
      )
    this.center = center
  }
}
