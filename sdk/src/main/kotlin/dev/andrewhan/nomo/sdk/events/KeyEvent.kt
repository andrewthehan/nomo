package dev.andrewhan.nomo.sdk.events

import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.core.Event

abstract class KeyEvent(open val key: Key) : Event

data class KeyPressEvent(override val key: Key) : KeyEvent(key)

data class KeyReleaseEvent(override val key: Key) : KeyEvent(key)

data class KeyHoldEvent(override val key: Key) : KeyEvent(key)
