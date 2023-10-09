package dev.andrewhan.nomo.boot.xp.systems

import dev.andrewhan.nomo.boot.xp.components.LevelComponent
import dev.andrewhan.nomo.boot.xp.events.ExperienceEvent
import dev.andrewhan.nomo.boot.xp.events.LevelUpEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getAssignableComponent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class ExperienceGainedSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<ExperienceEvent>() {
  override suspend fun handle(event: ExperienceEvent) {
    val levelComponent = engine.getAssignableComponent<LevelComponent>(event.entity)
    levelComponent.addExperience(event.amount)
    while (levelComponent.shouldLevel()) {
      levelComponent.levelUp()
      engine.dispatchEvent(LevelUpEvent(event.entity))
    }
  }
}
