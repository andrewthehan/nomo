package dev.andrewhan.nomo.sdk.systems

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class NomoSystem<EventType : Event>(replay: Int = 128) :
  System<EventType> {
  private val events: MutableSharedFlow<EventType> = MutableSharedFlow(replay)

  val output: SharedFlow<EventType> = events.asSharedFlow()

  final override suspend fun handle(event: EventType) {
    orderedHandle(event)
  }

  suspend fun emit(event: EventType) {
    events.emit(event)
  }

  abstract suspend fun orderedHandle(event: EventType)
}
