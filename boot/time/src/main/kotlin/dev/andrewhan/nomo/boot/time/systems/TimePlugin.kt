package dev.andrewhan.nomo.boot.time.systems

import dev.andrewhan.nomo.sdk.engines.EnginePlugin
import dev.andrewhan.nomo.sdk.events.UpdateEvent

val TimePlugin: EnginePlugin = {
  forEvent<UpdateEvent> {
    run<DelayedActionSystem>()
    run<PeriodicActionSystem>()
  }
}
