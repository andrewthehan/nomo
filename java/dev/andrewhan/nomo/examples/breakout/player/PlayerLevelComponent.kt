package dev.andrewhan.nomo.examples.breakout.player

import dev.andrewhan.nomo.boot.xp.components.LevelComponent

class PlayerLevelComponent : LevelComponent() {
  override fun shouldLevel(): Boolean {
    return experience >= level * 100
  }
}
