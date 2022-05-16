package dev.andrewhan.nomo.integration.libgdx.events

import com.badlogic.gdx.graphics.Camera
import dev.andrewhan.nomo.sdk.events.RenderEvent

data class LibgdxRenderEvent(val camera: Camera) : RenderEvent()
