package dev.andrewhan.nomo.core

interface System<in EventType : Event> {
  suspend fun handle(event: EventType)
}
