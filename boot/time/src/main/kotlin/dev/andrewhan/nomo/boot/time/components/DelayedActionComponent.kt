package dev.andrewhan.nomo.boot.time.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.sdk.components.Pendant
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DelayedActionComponent(
  private val delay: Duration = ZERO,
  private val action: suspend (Entity, NomoEngine) -> Boolean
) : Component, Pendant {
  private var elapsed: Duration = ZERO
  private val mutex = Mutex()

  suspend fun update(elapsed: Duration, engine: NomoEngine) {
    mutex.withLock {
      if (!engine.contains(this)) {
        return
      }

      this.elapsed += elapsed
      if (this.elapsed >= delay) {
        if (action(engine.getEntity(this), engine)) {
          engine.remove(this)
        }
      }
    }
  }

  override fun toString(): String = "${this::class.simpleName}(delay=$delay,elapsed=$elapsed)"
}
