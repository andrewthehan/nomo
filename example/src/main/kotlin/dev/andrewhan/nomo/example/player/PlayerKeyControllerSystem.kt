package dev.andrewhan.nomo.example.player

import com.badlogic.gdx.math.Vector2
import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.integration.libgdx.physics.Direction
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.io.KeyEvent
import dev.andrewhan.nomo.sdk.io.KeyPressEvent
import dev.andrewhan.nomo.sdk.io.KeyReleaseEvent
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntityOrNull
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import ktx.math.plus
import ktx.math.times
import javax.inject.Inject

class PlayerKeyControllerSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<KeyEvent>() {
  private val heldKeys = mutableSetOf<Key>()

  private fun Key.isDirection(): Boolean =
    when (this) {
      Key.UP -> true
      Key.W -> true
      Key.DOWN -> true
      Key.S -> true
      else -> false
    }

  private fun Key.toDirection(): Vector2 =
    when (this) {
      Key.UP -> Direction.UP
      Key.W -> Direction.UP
      Key.DOWN -> Direction.DOWN
      Key.S -> Direction.DOWN
      else -> {
        throw IllegalArgumentException("$this is not a direction")
      }
    }

  override suspend fun handle(event: KeyEvent) {
    val entity = engine.getEntityOrNull(PlayerComponent) ?: return
    when (event) {
      is KeyPressEvent ->
        if (event.key.isDirection()) {
          heldKeys.add(event.key)
        }
      is KeyReleaseEvent ->
        if (event.key.isDirection()) {
          heldKeys.remove(event.key)
        }
      else -> {}
    }

    val direction = heldKeys.map { it.toDirection() }.fold(Vector2.Zero) { acc, v -> acc + v }.nor()
    val velocity = direction * 5f
    engine.getComponent<BodyComponent>(entity).body.linearVelocity = velocity
  }
}
