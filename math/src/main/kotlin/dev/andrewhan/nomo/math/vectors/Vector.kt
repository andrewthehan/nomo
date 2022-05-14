package dev.andrewhan.nomo.math.vectors

import dev.andrewhan.nomo.math.cast
import dev.andrewhan.nomo.math.div
import dev.andrewhan.nomo.math.isZero
import dev.andrewhan.nomo.math.minus
import dev.andrewhan.nomo.math.plus
import dev.andrewhan.nomo.math.sum
import dev.andrewhan.nomo.math.times
import dev.andrewhan.nomo.math.unaryMinus
import kotlin.math.sqrt

interface Vector<NumberType : Number> {
  val dimensions: Int
  val components: List<NumberType>
  val vectorProducer: (elementProvider: (Int) -> NumberType) -> Vector<NumberType>
}

@Suppress("UNCHECKED_CAST")
fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.create(
  elementProvider: (Int) -> NumberType
): VectorType {
  return vectorProducer(elementProvider) as VectorType
}

fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.create(
  vararg elements: NumberType
): VectorType = create { elements[it] }

fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.create(
  elements: List<NumberType>
): VectorType = create { elements[it] }

operator fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.unaryPlus():
  VectorType = create { this[it] }

operator fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.unaryMinus():
  VectorType = create { -this[it] }

operator fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.plus(
  vector: VectorType
): VectorType = create(components.zip(vector.components) { a, b -> a + b })

operator fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.minus(
  vector: VectorType
): VectorType = create(components.zip(vector.components) { a, b -> a - b })

operator fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.times(
  scalar: NumberType
): VectorType = map { it * scalar }

operator fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.div(
  scalar: NumberType
): VectorType = map { it / scalar }

infix fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.dot(
  vector: VectorType
): NumberType = components.zip(vector.components) { a, b -> a * b }.sum()

operator fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.get(
  i: Int
): NumberType = components[i]

fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.length(): Float =
  sqrt(map { it * it }.components.sum().toFloat())

fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.normalized(): VectorType {
  val length = length()
  return map { (it.toFloat() / length).cast() }
}

val Vector<*>.isZero: Boolean
  get() = components.all { it.isZero() }

fun <NumberType : Number, VectorType : Vector<NumberType>> VectorType.map(
  transform: (NumberType) -> NumberType
): VectorType = create { transform(this[it]) }

operator fun <NumberType : Number, VectorType : Vector<NumberType>> NumberType.times(
  vector: VectorType
): VectorType {
  return vector.map { this * it }
}

operator fun <NumberType : Number, VectorType : Vector<NumberType>> NumberType.div(
  vector: VectorType
): VectorType {
  return vector.map { this / it }
}
