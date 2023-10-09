package dev.andrewhan.nomo.sdk.systems

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import dev.andrewhan.nomo.sdk.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class SystemFeatures {
  SWALLOW_EVENTS,
  CONCURRENT
}

abstract class NomoSystem<EventType : Event>(private vararg val features: SystemFeatures) :
  System<EventType>, Lifecycle {
  private val mutableEvents: MutableSharedFlow<EventType> = MutableSharedFlow(replay = 128)

  val events: SharedFlow<EventType> = mutableEvents.asSharedFlow()
  val subscriptionCount: StateFlow<Int> = mutableEvents.subscriptionCount

  private val action by lazy {
    var builder: suspend (EventType) -> Unit = { handle(it) }

    if (!features.contains(SystemFeatures.SWALLOW_EVENTS)) {
      val original = builder
      builder = {
        original(it)
        emit(it)
      }
    }

    if (!features.contains(SystemFeatures.CONCURRENT)) {
      val mutex = Mutex()
      val original = builder
      builder = { mutex.withLock { original(it) } }
    }

    builder
  }

  fun subscribe(scope: CoroutineScope, sourceEvents: Flow<EventType>): Job {
    return sourceEvents.onEach(action).launchIn(scope)
  }

  suspend fun emit(event: EventType) {
    mutableEvents.emit(event)
  }
}
