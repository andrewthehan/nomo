package dev.andrewhan.nomo.sdk.stores

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.Store
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

inline fun <reified EventType : Event> EventStore.flowFor(): Flow<EventType> =
  flow().filter { it is EventType }.map { it as EventType }.buffer()

fun <EventType : Event> EventStore.flowFor(eventType: Class<EventType>): Flow<EventType> =
  flow()
    .filter { eventType.isInstance(it) }
    .map {
      @Suppress("UNCHECKED_CAST") // checked by filter above
      it as EventType
    }
    .buffer()

fun <EventType : Event> EventStore.flowFor(eventType: KClass<EventType>): Flow<EventType> =
  flowFor(eventType.java)

interface EventStore : Store {
  fun flow(): SharedFlow<Event>

  suspend fun dispatchEvent(event: Event)
}

internal class NomoEventStore(replay: Int = 128) : EventStore {
  private val events: MutableSharedFlow<Event> = MutableSharedFlow(replay)

  override fun flow(): SharedFlow<Event> = events.asSharedFlow()

  override suspend fun dispatchEvent(event: Event) {
    events.emit(event)
  }
}
