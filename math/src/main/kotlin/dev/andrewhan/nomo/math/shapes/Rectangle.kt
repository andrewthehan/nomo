package dev.andrewhan.nomo.math.shapes

import dev.andrewhan.nomo.math.vectors.Direction
import dev.andrewhan.nomo.math.vectors.Vector2f
import dev.andrewhan.nomo.math.vectors.plus
import dev.andrewhan.nomo.math.vectors.times

data class Rectangle(override val center: Vector2f, val width: Float, val height: Float) : Shape {
  override val points: List<Vector2f>

  init {
    val halfWidth = width / 2
    val halfHeight = height / 2
    this.points =
      listOf(
        center + Direction.UP * halfHeight + Direction.LEFT * halfWidth,
        center + Direction.UP * halfHeight + Direction.RIGHT * halfWidth,
        center + Direction.DOWN * halfHeight + Direction.RIGHT * halfWidth,
        center + Direction.DOWN * halfHeight + Direction.LEFT * halfWidth,
      )
  }
}
