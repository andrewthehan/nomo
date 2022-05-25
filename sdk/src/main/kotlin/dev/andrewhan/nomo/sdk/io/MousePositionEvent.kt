package dev.andrewhan.nomo.sdk.io

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.sdk.util.Location

data class MousePositionEvent(val location: Location) : Event