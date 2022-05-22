package dev.andrewhan.nomo.integration.libgdx.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputMultiplexer
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.KeyHoldEvent
import dev.andrewhan.nomo.sdk.events.KeyPressEvent
import dev.andrewhan.nomo.sdk.events.KeyReleaseEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import ktx.app.KtxInputAdapter
import javax.inject.Inject

fun toKey(keyCode: Int): Key =
  when (keyCode) {
    Keys.A -> Key.A
    Keys.B -> Key.B
    Keys.C -> Key.C
    Keys.D -> Key.D
    Keys.E -> Key.E
    Keys.F -> Key.F
    Keys.G -> Key.G
    Keys.H -> Key.H
    Keys.I -> Key.I
    Keys.J -> Key.J
    Keys.K -> Key.K
    Keys.L -> Key.L
    Keys.M -> Key.M
    Keys.N -> Key.N
    Keys.O -> Key.O
    Keys.P -> Key.P
    Keys.Q -> Key.Q
    Keys.R -> Key.R
    Keys.S -> Key.S
    Keys.T -> Key.T
    Keys.U -> Key.U
    Keys.V -> Key.V
    Keys.W -> Key.W
    Keys.X -> Key.X
    Keys.Y -> Key.Y
    Keys.Z -> Key.Z
    Keys.SPACE -> Key.SPACE
    Keys.CONTROL_LEFT -> Key.CONTROL_LEFT
    Keys.CONTROL_RIGHT -> Key.CONTROL_RIGHT
    Keys.ALT_LEFT -> Key.ALT_LEFT
    Keys.ALT_RIGHT -> Key.ALT_RIGHT
    Keys.SHIFT_LEFT -> Key.SHIFT_LEFT
    Keys.SHIFT_RIGHT -> Key.SHIFT_RIGHT
    Keys.UP -> Key.UP
    Keys.DOWN -> Key.DOWN
    Keys.LEFT -> Key.LEFT
    Keys.RIGHT -> Key.RIGHT
    else ->
      throw UnsupportedOperationException(
        "Unsupported keyCode: ${Keys.toString(keyCode)} (${keyCode})"
      )
  }

class KeyInputSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  private val heldKeys = HashSet<Key>()

  override suspend fun start() {
    var multiplexer = Gdx.input.inputProcessor
    if (multiplexer == null) {
      multiplexer = InputMultiplexer()
      Gdx.input.inputProcessor = multiplexer
    } else if (multiplexer !is InputMultiplexer) {
      val processor = multiplexer
      multiplexer = InputMultiplexer()
      multiplexer.addProcessor(processor)
      Gdx.input.inputProcessor = multiplexer
    }
    multiplexer.addProcessor(
      object : KtxInputAdapter {
        override fun keyDown(keycode: Int): Boolean {
          val key = toKey(keycode)
          runBlocking { engine.dispatchEvent(KeyPressEvent(key)) }
          heldKeys.add(key)
          return true
        }

        override fun keyUp(keycode: Int): Boolean {
          val key = toKey(keycode)
          runBlocking { engine.dispatchEvent(KeyReleaseEvent(key)) }
          heldKeys.remove(key)
          return true
        }
      }
    )
  }

  override suspend fun handle(event: UpdateEvent) {
    heldKeys.forEach { engine.dispatchEvent(KeyHoldEvent(it)) }
  }
}
