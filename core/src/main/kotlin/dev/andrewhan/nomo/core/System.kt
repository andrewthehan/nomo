package dev.andrewhan.nomo.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface System<EventType : Event> {
  suspend fun start(events: Flow<EventType>) = events.collect(this::handle)

  suspend fun handle(event: EventType)

  fun flow(): SharedFlow<EventType>
}
