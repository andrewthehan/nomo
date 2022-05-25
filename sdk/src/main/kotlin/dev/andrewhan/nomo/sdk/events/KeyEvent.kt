package dev.andrewhan.nomo.sdk.events

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.sdk.io.Key

sealed interface KeyEvent : Event {
  val key: Key
}

data class KeyPressEvent(override val key: Key) : KeyEvent

data class KeyReleaseEvent(override val key: Key) : KeyEvent

data class KeyHoldEvent(override val key: Key) : KeyEvent
