package dev.andrewhan.nomo.boot.xp.systems

import dev.andrewhan.nomo.boot.xp.events.ExperienceEvent
import dev.andrewhan.nomo.boot.xp.events.LevelUpEvent
import dev.andrewhan.nomo.sdk.engines.EnginePlugin

val LevelPlugin: EnginePlugin = {
  forEvent<ExperienceEvent> { run<ExperienceGainedSystem>() }
}
