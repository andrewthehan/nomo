package dev.andrewhan.nomo.example.items.xp

import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.physics.box2d.BodyDef
import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.example.GameBounds
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.entities.entity
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.Size
import ktx.box2d.box
import javax.inject.Inject

class ExperienceItemGenerationSystem
@Inject
constructor(private val engine: NomoEngine, @GameBounds private val gameBounds: Size) :
  NomoSystem<UpdateEvent>() {
  override suspend fun handle(event: UpdateEvent) {
    if (engine.getComponents<ExperienceItemComponent>().isNotEmpty()) return

    val player = engine.getEntity(PlayerComponent)
    val body = engine.getComponent<BodyComponent>(player)
    engine.entity(
      ExperienceItemComponent(50),
      BodyComponent(body.world) {
        type = BodyDef.BodyType.StaticBody
        position.set(
          random(-gameBounds.width / 2, gameBounds.width / 2),
          random(-gameBounds.height / 2, gameBounds.height / 2)
        )
        box(width = 0.4f, height = 0.4f) { isSensor = true }
      }
    )
  }
}
