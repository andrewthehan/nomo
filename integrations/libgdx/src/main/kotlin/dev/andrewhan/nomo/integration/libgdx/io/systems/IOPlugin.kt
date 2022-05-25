package dev.andrewhan.nomo.integration.libgdx.io.systems

import dev.andrewhan.nomo.sdk.engines.EnginePlugin
import dev.andrewhan.nomo.sdk.events.UpdateEvent

val IOPlugin: EnginePlugin = {
  forEvent<UpdateEvent> {
    run<KeyInputSystem>()
    run<MouseInputSystem>()
  }
}
