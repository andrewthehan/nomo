package dev.andrewhan.nomo.sdk.events

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.sdk.io.MouseButton
import dev.andrewhan.nomo.sdk.util.Location

sealed interface MouseButtonEvent : Event {
  val mouseButton: MouseButton
  val location: Location
}

data class MousePressButtonEvent(
  override val mouseButton: MouseButton,
  override val location: Location
) : MouseButtonEvent

data class MouseReleaseButtonEvent(
  override val mouseButton: MouseButton,
  override val location: Location
) : MouseButtonEvent

data class MouseHoldButtonEvent(
  override val mouseButton: MouseButton,
  override val location: Location
) : MouseButtonEvent
