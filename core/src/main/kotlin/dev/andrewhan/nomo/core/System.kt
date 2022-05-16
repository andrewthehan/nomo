package dev.andrewhan.nomo.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface System<EventType : Event> {
  suspend fun start(scope: CoroutineScope, events: Flow<EventType>): Job =
    events.onEach(this::handle).launchIn(scope)

  suspend fun handle(event: EventType)

  fun flow(): SharedFlow<EventType>
}
