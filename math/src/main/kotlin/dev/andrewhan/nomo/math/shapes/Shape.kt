package dev.andrewhan.nomo.math.shapes

import dev.andrewhan.nomo.math.vectors.Vector2f

interface Shape {
  val points: List<Vector2f>
  val center: Vector2f
}
