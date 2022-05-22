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

abstract class NomoSystem<EventType : Event>(private val propagate: Boolean = true) :
  System<EventType>, Lifecycle {
  private val mutableEvents: MutableSharedFlow<EventType> = MutableSharedFlow()

  val events: SharedFlow<EventType> = mutableEvents.asSharedFlow()

  val subscriptionCount: StateFlow<Int> = mutableEvents.subscriptionCount

  suspend fun subscribe(scope: CoroutineScope, sourceEvents: Flow<EventType>): Job =
    if (propagate) {
      sourceEvents
        .onEach {
          handle(it)
          emit(it)
        }
        .launchIn(scope)
    } else {
      sourceEvents.onEach { handle(it) }.launchIn(scope)
    }

  suspend fun emit(event: EventType) {
    mutableEvents.emit(event)
  }
}
