package dev.andrewhan.nomo.example.player

import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.boot.xp.events.LevelUpEvent
import dev.andrewhan.nomo.example.walls.WallSegmentRegenerateEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class PlayerLevelUpSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<LevelUpEvent>() {
  override suspend fun handle(event: LevelUpEvent) {
    val player = engine.getEntity(PlayerComponent)
    val stats = engine.getComponent<PlayerStatsComponent>(player)
    stats.segments++

    engine.dispatchEvent(WallSegmentRegenerateEvent)
  }
}
