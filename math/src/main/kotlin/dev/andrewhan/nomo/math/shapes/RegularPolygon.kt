package dev.andrewhan.nomo.math.shapes

import dev.andrewhan.nomo.math.vectors.Vector2f
import dev.andrewhan.nomo.math.vectors.plus
import dev.andrewhan.nomo.math.vectors.times
import dev.andrewhan.nomo.math.vectors.vectorOf
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun rootsOfUnity(n: Int): List<Vector2f> {
  return (0 until n).map {
    vectorOf(cos(it * 2 * PI / n).toFloat(), sin(it * 2 * PI / n).toFloat())
  }
}

open class RegularPolygon : Shape {
  override val points: List<Vector2f>
  override val center: Vector2f

  open val radius: Float

  constructor(center: Vector2f, radius: Float, n: Int) {
    this.points = rootsOfUnity(n).map { point -> point * radius + center }
    this.center = center
    this.radius = radius
  }

  override fun toString(): String {
    return "${RegularPolygon::class.simpleName}(points=$points,center=$center)"
  }
}
