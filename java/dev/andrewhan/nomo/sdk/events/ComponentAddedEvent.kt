package dev.andrewhan.nomo.sdk.events

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Event

data class ComponentAddedEvent(val component: Component) : Event
