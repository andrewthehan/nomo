package dev.andrewhan.nomo.integration.libgdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.OrthographicCamera
import dev.andrewhan.nomo.integration.libgdx.events.RenderEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import kotlinx.coroutines.runBlocking
import ktx.app.KtxApplicationAdapter
import ktx.app.KtxInputAdapter
import ktx.async.KtxAsync
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
class Game(
  private val title: String,
  private val width: Int,
  private val height: Int,
  private val engine: NomoEngine
) : KtxApplicationAdapter, KtxInputAdapter {
  private val camera by lazy {
    OrthographicCamera().apply { setToOrtho(false, width.toFloat(), height.toFloat()) }
  }

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
    KtxAsync.initiate()
    runBlocking { engine.start() }
  }

  override fun render() {
    camera.update()
    runBlocking {
      engine.dispatchEvent(UpdateEvent(Gdx.graphics.deltaTime.toDouble().seconds))
      engine.dispatchEvent(RenderEvent(camera))
    }
  }
}
