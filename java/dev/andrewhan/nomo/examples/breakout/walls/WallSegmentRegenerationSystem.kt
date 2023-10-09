package dev.andrewhan.nomo.examples.breakout.walls

import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntities
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class WallSegmentRegenerationSystem @Inject constructor(private val engine: NomoEngine) :
    NomoSystem<WallSegmentRegenerateEvent>() {
  override suspend fun handle(event: WallSegmentRegenerateEvent) {
    engine.getEntities<WallSegmentComponent>().forEach { engine.remove(it) }

    val player = engine.getEntity(PlayerComponent)
    val playerBody = engine.getComponent<BodyComponent>(player)
    engine.newWallSegments(playerBody.world)
  }
}
