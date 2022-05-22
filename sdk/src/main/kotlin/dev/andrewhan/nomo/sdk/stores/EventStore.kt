package dev.andrewhan.nomo.sdk.stores

import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

inline fun <reified EventType : Event> EventStore.flowFor(): Flow<EventType> =
  events.filter { it is EventType }.map { it as EventType }

fun <EventType : Event> EventStore.flowFor(eventType: Class<EventType>): Flow<EventType> =
  events
    .filter { eventType.isInstance(it) }
    .map {
      @Suppress("UNCHECKED_CAST") // checked by filter above
      it as EventType
    }

fun <EventType : Event> EventStore.flowFor(eventType: KClass<EventType>): Flow<EventType> =
  flowFor(eventType.java)

interface EventStore : Store {
  val subscriptionCount: StateFlow<Int>

  val events: SharedFlow<Event>

  suspend fun dispatchEvent(event: Event)
}

internal class NomoEventStore(replay: Int = 128) : EventStore {
  private val mutableEvents: MutableSharedFlow<Event> = MutableSharedFlow(replay)

  override val events: SharedFlow<Event> = mutableEvents.asSharedFlow()

  override val subscriptionCount: StateFlow<Int> = mutableEvents.subscriptionCount

  override suspend fun dispatchEvent(event: Event) {
    mutableEvents.emit(event)
  }
}
