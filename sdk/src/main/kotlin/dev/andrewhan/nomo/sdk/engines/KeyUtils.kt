package dev.andrewhan.nomo.sdk.engines

import com.google.inject.Key
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import dev.andrewhan.nomo.sdk.stores.EventStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

inline fun <reified T> key(annotation: KClass<out Annotation>? = null): Key<T> =
  if (annotation == null) {
    object : Key<T>() {}
  } else {
    object : Key<T>(annotation.java) {}
  }

@Suppress("UNCHECKED_CAST") // System has one Event type parameter
inline fun <reified T : System<*>> systemKey(): Key<System<Event>> = key<T>() as Key<System<Event>>

fun <EventType : Event> EventStore.flowFor(key: Key<EventType>): Flow<EventType> =
  flow()
    .filter { key.typeLiteral.rawType.isInstance(it) }
    .map {
      @Suppress("UNCHECKED_CAST") // checked using key above
      it as EventType
    }
