package dev.andrewhan.nomo.boot.time.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.sdk.components.Pendant
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getEntity
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PeriodicActionComponent(
  private val delay: Duration,
  private val action: (Entity, NomoEngine) -> Unit
) : Component, Pendant {
  private var elapsed: Duration = ZERO

  init {
    check(delay > ZERO) { "Requires a non zero delay: $delay" }
  }

  fun update(elapsed: Duration, engine: NomoEngine) {
    synchronized(this) {
      if (!engine.contains(this)) {
        return
      }

      this.elapsed += elapsed
      while (this.elapsed >= delay) {
        action(engine.getEntity(this), engine)
        this.elapsed -= delay
      }
    }
  }

  override fun toString(): String = "${this::class.simpleName}(delay=$delay,elapsed=$elapsed)"
}
