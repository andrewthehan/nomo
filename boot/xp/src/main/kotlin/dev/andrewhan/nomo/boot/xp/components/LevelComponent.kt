package dev.andrewhan.nomo.boot.xp.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant

abstract class LevelComponent(var level: Int = 1) : Component, Pendant, Exclusive {
  private var internalExperience: Int = 0
  val experience
    get() = internalExperience

  abstract fun shouldLevel(): Boolean

  fun addExperience(amount: Int) {
    synchronized(this) { internalExperience += amount }
  }

  fun levelUp() {
    level++
  }
}
