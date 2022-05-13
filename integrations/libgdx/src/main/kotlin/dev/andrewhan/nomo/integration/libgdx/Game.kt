package dev.andrewhan.nomo.integration.libgdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dev.andrewhan.nomo.core.Engine
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.runBlocking
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use

@ExperimentalTime
class Game(
  private val title: String,
  private val width: Int,
  private val height: Int,
  private val engine: Engine
) : KtxGame<KtxScreen>() {
  fun start() {
    val config =
      LwjglApplicationConfiguration().apply {
        title = this@Game.title
        width = this@Game.width
        height = this@Game.height
      }
    LwjglApplication(this, config).logLevel = Application.LOG_DEBUG
  }

  override fun create() {
    addScreen(GameScreen(this))
    setScreen<GameScreen>()
    super.create()

    runBlocking { engine.start() }
  }

  private class GameScreen(val game: Game) : KtxScreen {
    private val batch = SpriteBatch()
    private val font = BitmapFont()

    private val camera =
      OrthographicCamera().apply { setToOrtho(false, game.width.toFloat(), game.height.toFloat()) }

    override fun render(delta: Float) {
      camera.update()
      batch.use(camera) { stepEngine(delta) }
    }

    override fun dispose() {
      batch.dispose()
      font.dispose()
    }

    private fun stepEngine(delta: Float) {
      runBlocking {
        game.engine.update(delta.toDouble().seconds)
        font.draw(batch, "Hello world!", 100f, 150f)
      }
    }
  }
}
