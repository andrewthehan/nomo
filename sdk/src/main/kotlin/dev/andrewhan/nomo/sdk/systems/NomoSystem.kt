package dev.andrewhan.nomo.sdk.systems

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class NomoSystem<EventType : Event>(private val propagate: Boolean = true) :
  System<EventType> {
  private val events: MutableSharedFlow<EventType> = MutableSharedFlow()

  override suspend fun start(scope: CoroutineScope, events: Flow<EventType>): Job =
    if (propagate) {
      events
        .onEach {
          handle(it)
          emit(it)
        }
        .launchIn(scope)
    } else {
      super.start(scope, events)
    }

  override fun flow(): SharedFlow<EventType> = events.asSharedFlow()

  suspend fun emit(event: EventType) {
    events.emit(event)
  }
}
