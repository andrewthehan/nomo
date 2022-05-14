package dev.andrewhan.nomo.sdk.engines

import com.google.inject.Key
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import dev.andrewhan.nomo.sdk.util.getAllAssignableTypes
import java.lang.reflect.ParameterizedType

data class SystemMetadata<EventType : Event, SystemType : System<EventType>>(
  val systemKey: Key<SystemType>
) {
  @Suppress("UNCHECKED_CAST") // System has one Event type parameter
  val eventKey: Key<EventType> by lazy {
    Key.get(systemKey.typeLiteral.rawType.getAllAssignableTypes().asSequence()
      .filterIsInstance<ParameterizedType>().filter { it.rawType is Class<*> }
      .filter { System::class.java.isAssignableFrom(it.rawType as Class<*>) }
      .map { it.actualTypeArguments[0] }.filterIsInstance<Class<*>>().single()) as Key<EventType>
  }
}