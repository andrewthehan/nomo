package dev.andrewhan.nomo.integration.libgdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import dev.andrewhan.nomo.integration.libgdx.render.events.RenderEvent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.TimeStep
import dev.andrewhan.nomo.sdk.engines.key
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import kotlinx.coroutines.runBlocking
import ktx.app.KtxApplicationAdapter
import ktx.async.KtxAsync
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

fun game(engine: NomoEngine, builder: GameBuilder.() -> Unit = {}): Game {
  return GameBuilder(engine).apply(builder).build()
}

class GameBuilder(private val engine: NomoEngine) {
  var title: String = "Game"
  var width: Int = 1366
  var height: Int = 768

  fun build(): Game {
    return Game(title, width, height, engine)
  }
}

@OptIn(ExperimentalTime::class)
class Game(
  private val title: String,
  private val width: Int,
  private val height: Int,
  private val engine: NomoEngine
) : KtxApplicationAdapter {

  private val timeStep: Duration = engine.getInstance(key<Duration>(TimeStep::class))
  private var accumulator: Duration = ZERO

  private lateinit var app: Lwjgl3Application

  fun start() {
    val config =
      Lwjgl3ApplicationConfiguration().apply {
        setTitle(title)
        setWindowedMode(width, height)
      }
    app = Lwjgl3Application(this, config).apply { logLevel = Application.LOG_DEBUG }
  }

  override fun create() {
    KtxAsync.initiate()
    runBlocking { engine.start() }
  }

  override fun render() {
    runBlocking {
      accumulator += Gdx.graphics.deltaTime.toDouble().seconds
      while (accumulator >= timeStep) {
        engine.dispatchEvent(UpdateEvent(timeStep))
        accumulator -= timeStep
      }
      engine.dispatchEvent(RenderEvent)
    }
  }
}
