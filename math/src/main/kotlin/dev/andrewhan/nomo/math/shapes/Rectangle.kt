package dev.andrewhan.nomo.math.shapes

import dev.andrewhan.nomo.math.vectors.DOWN
import dev.andrewhan.nomo.math.vectors.LEFT
import dev.andrewhan.nomo.math.vectors.RIGHT
import dev.andrewhan.nomo.math.vectors.UP
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
        center + UP * halfHeight + LEFT * halfWidth,
        center + UP * halfHeight + RIGHT * halfWidth,
        center + DOWN * halfHeight + RIGHT * halfWidth,
        center + DOWN * halfHeight + LEFT * halfWidth,
      )
  }
}
