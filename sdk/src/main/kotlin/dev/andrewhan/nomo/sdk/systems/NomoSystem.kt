package dev.andrewhan.nomo.sdk.systems

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class NomoSystem<EventType : Event>(
  private val propagate: Boolean = true,
  replay: Int = 128
) : System<EventType> {
  private val events: MutableSharedFlow<EventType> = MutableSharedFlow(replay)

  override suspend fun start(events: Flow<EventType>) {
    if (propagate) {
      events.collect {
        handle(it)
        emit(it)
      }
    } else {
      super.start(events)
    }
  }

  override fun flow(): SharedFlow<EventType> = events.asSharedFlow()

  suspend fun emit(event: EventType) {
    events.emit(event)
  }
}
