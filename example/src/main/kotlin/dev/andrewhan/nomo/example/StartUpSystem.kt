package dev.andrewhan.nomo.example

import com.badlogic.gdx.math.Vector2
import dev.andrewhan.nomo.example.ball.newBall
import dev.andrewhan.nomo.example.player.newPlayer
import dev.andrewhan.nomo.example.walls.newWalls
import dev.andrewhan.nomo.integration.libgdx.physics.createSafeWorld
import dev.andrewhan.nomo.integration.libgdx.render.components.CameraComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.Location
import dev.andrewhan.nomo.sdk.util.Size
import javax.inject.Inject

class StartUpSystem
@Inject
constructor(private val engine: NomoEngine, @GameBounds private val gameBounds: Size) :
  NomoSystem<NeverEvent>() {
  override suspend fun start() {
    val world = createSafeWorld("main world")
    engine.apply {
      "camera" bind CameraComponent(Location(), Size(1366f, 768f), Vector2(), 0.01f)
      newPlayer(world)
      newBall(world)
      newWalls(world)

      newDeleteZone(
        world,
        Vector2(-gameBounds.width / 2f - 0.5f, 0f),
        Size(0.1f, gameBounds.height)
      )
    }
  }

  override suspend fun handle(event: NeverEvent) {}
}
