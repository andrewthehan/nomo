package dev.andrewhan.nomo.integration.libgdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.OrthographicCamera
import dev.andrewhan.nomo.integration.libgdx.events.LibgdxRenderEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ktx.app.KtxApplicationAdapter
import ktx.async.KTX
import ktx.async.KtxAsync
import ktx.async.newAsyncContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
class Game(
  private val title: String,
  private val width: Int,
  private val height: Int,
  private val engine: NomoEngine
) : KtxApplicationAdapter {
  private val camera by lazy {
    OrthographicCamera().apply { setToOrtho(false, width.toFloat(), height.toFloat()) }
  }

  private val eventScope: CoroutineScope = CoroutineScope(newAsyncContext(1, "EventPropagation"))
  private val uiScope: CoroutineScope = CoroutineScope(Dispatchers.KTX)

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
    runBlocking { withContext(KtxAsync.coroutineContext) { engine.start(eventScope, uiScope) } }
  }

  override fun render() {
    camera.update()
    runBlocking {
      withContext(KtxAsync.coroutineContext) {
        engine.dispatchEvent(UpdateEvent(Gdx.graphics.deltaTime.toDouble().seconds))
        engine.dispatchEvent(LibgdxRenderEvent(camera))
      }
    }
  }
}
