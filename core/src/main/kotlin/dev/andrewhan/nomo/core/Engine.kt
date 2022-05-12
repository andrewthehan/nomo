package dev.andrewhan.nomo.core

interface Engine : Updatable {
  suspend fun start() = Unit
  suspend fun stop() = Unit
}
