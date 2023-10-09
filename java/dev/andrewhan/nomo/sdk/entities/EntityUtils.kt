package dev.andrewhan.nomo.sdk.entities

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.sdk.components.ComponentPackage
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import java.util.UUID

fun newEntity(): Entity = UUID.randomUUID().toString()

fun NomoEngine.entity(vararg components: Component): Entity {
  return entity(ComponentPackage(*components))
}

fun NomoEngine.entity(componentPackage: ComponentPackage): Entity {
  check(componentPackage.components.isNotEmpty()) { "Component package cannot be empty" }
  val entity = newEntity()
  apply { entity bind componentPackage }
  return entity
}
