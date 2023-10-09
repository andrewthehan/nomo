package dev.andrewhan.nomo.boot.player.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant

object PlayerComponent : Component, Pendant, Exclusive {
  override fun toString(): String = "${this::class.simpleName}"
}
