package dev.andrewhan.nomo.example.entities.player

import com.badlogic.gdx.math.Vector2
import dev.andrewhan.nomo.integration.libgdx.physics.Direction
import dev.andrewhan.nomo.integration.libgdx.physics.events.ForceEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.KeyEvent
import dev.andrewhan.nomo.sdk.events.KeyPressEvent
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import ktx.math.times
import javax.inject.Inject

class PlayerKeyControllerSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<KeyEvent>() {
  private val jumpForce = 500f

  private fun Key.isDirection(): Boolean =
    when (this) {
      Key.UP -> true
      Key.DOWN -> true
      Key.LEFT -> true
      Key.RIGHT -> true
      Key.W -> true
      Key.A -> true
      Key.S -> true
      Key.D -> true
      else -> false
    }

  private fun Key.toDirection(): Vector2 =
    when (this) {
      Key.UP -> Direction.UP
      Key.DOWN -> Direction.DOWN
      Key.LEFT -> Direction.LEFT
      Key.RIGHT -> Direction.RIGHT
      Key.W -> Direction.UP
      Key.A -> Direction.LEFT
      Key.S -> Direction.DOWN
      Key.D -> Direction.RIGHT
      else -> {
        throw IllegalArgumentException("$this is not a direction")
      }
    }

  override suspend fun handle(event: KeyEvent) {
    val entity = engine.getEntityOrNull(PlayerComponent) ?: return
    when (event) {
      //      is KeyHoldEvent ->
      //        if (event.key.isDirection()) {
      //          engine.dispatchEvent(ForceEvent(event.key.toDirection() * speed, entity))
      //        }
      is KeyPressEvent ->
        when (event.key) {
          Key.SPACE -> {
            engine.dispatchEvent(ForceEvent(Direction.UP * jumpForce, entity))
          }
          else -> {}
        }
      else -> {}
    }
  }
}
