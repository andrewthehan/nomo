package dev.andrewhan.nomo.integration.libgdx.render.events

import com.badlogic.gdx.graphics.Camera
import dev.andrewhan.nomo.core.Event

data class RenderEvent(val camera: Camera) : Event
