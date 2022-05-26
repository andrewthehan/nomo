package dev.andrewhan.nomo.integration.libgdx.io.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.InputMultiplexer
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.io.MouseButton
import dev.andrewhan.nomo.sdk.io.MouseHoldButtonEvent
import dev.andrewhan.nomo.sdk.io.MousePositionEvent
import dev.andrewhan.nomo.sdk.io.MousePressButtonEvent
import dev.andrewhan.nomo.sdk.io.MouseReleaseButtonEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.Location
import kotlinx.coroutines.runBlocking
import ktx.app.KtxInputAdapter
import javax.inject.Inject

fun toMouseButton(mouseButton: Int): MouseButton =
  when (mouseButton) {
    Buttons.LEFT -> MouseButton.LEFT
    Buttons.RIGHT -> MouseButton.RIGHT
    Buttons.MIDDLE -> MouseButton.MIDDLE
    Buttons.BACK -> MouseButton.BACK
    Buttons.FORWARD -> MouseButton.FORWARD
    else -> throw UnsupportedOperationException("Unsupported mouse button: $mouseButton")
  }

class MouseInputSystem @Inject constructor(private val engine: NomoEngine) :
  NomoSystem<UpdateEvent>() {
  private val heldMouseButtons = HashSet<MouseButton>()
  private var lastLocation = Location()

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
        override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
          lastLocation = Location(screenX, Gdx.graphics.height - screenY)
          return true
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
          val mouseButton = toMouseButton(button)
          runBlocking {
            engine.dispatchEvent(
              MousePressButtonEvent(mouseButton, Location(screenX, Gdx.graphics.height - screenY))
            )
          }
          heldMouseButtons.add(mouseButton)
          return true
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
          lastLocation = Location(screenX, Gdx.graphics.height - screenY)
          return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
          val mouseButton = toMouseButton(button)
          runBlocking {
            engine.dispatchEvent(
              MouseReleaseButtonEvent(mouseButton, Location(screenX, Gdx.graphics.height - screenY))
            )
          }
          heldMouseButtons.remove(mouseButton)
          return true
        }
      }
    )
  }

  override suspend fun handle(event: UpdateEvent) {
    engine.dispatchEvent(MousePositionEvent(lastLocation))
    heldMouseButtons.forEach { engine.dispatchEvent(MouseHoldButtonEvent(it, lastLocation)) }
  }
}
