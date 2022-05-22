package dev.andrewhan.nomo.core

interface System<EventType : Event> {
  suspend fun handle(event: EventType)
}
