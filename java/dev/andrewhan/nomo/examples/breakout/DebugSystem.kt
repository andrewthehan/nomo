package dev.andrewhan.nomo.examples.breakout

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.io.Key
import dev.andrewhan.nomo.sdk.io.KeyPressEvent
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import javax.inject.Inject

class DebugSystem @Inject constructor(private val engine: NomoEngine) :
    NomoSystem<KeyPressEvent>() {
  override suspend fun handle(event: KeyPressEvent) {
    when (event.key) {
      Key.T -> {
        val message = buildString {
          engine.entities.sorted().forEach { entity ->
            appendLine(entity)
            engine[entity].sortedBy { it::class.simpleName }.forEach {
              appendLine("    ${it.toString().replace("${Component::class.simpleName}", "")}")
            }
          }
        }
        println(message)
      }
      else -> {}
    }
  }
}
