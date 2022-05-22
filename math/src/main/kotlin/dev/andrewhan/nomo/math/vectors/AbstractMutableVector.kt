package dev.andrewhan.nomo.math.vectors

abstract class AbstractMutableVector<NumberType : Number> :
  AbstractVector<NumberType>, MutableVector<NumberType> {
  override val dimensions: Int
  override val components: MutableList<NumberType>

  constructor(vararg elements: NumberType) {
    this.dimensions = elements.size
    this.components = MutableList(this.dimensions) { elements[it] }
  }
}