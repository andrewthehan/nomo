package dev.andrewhan.nomo.sdk.engines

import com.google.inject.Injector
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Engine
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.sdk.components.ComponentPackage
import dev.andrewhan.nomo.sdk.lifecycle.Lifecycle
import dev.andrewhan.nomo.sdk.stores.EntityComponentStore
import dev.andrewhan.nomo.sdk.stores.EventStore

interface NomoEngine : Engine, EntityComponentStore, EventStore, Lifecycle, Injector {
  infix fun Entity.bind(component: Component) {
    add(this, component)
  }

  infix fun Entity.bind(componentPackage: ComponentPackage) {
    add(this, componentPackage)
  }

  fun add(entity: Entity, componentPackage: ComponentPackage) {
    componentPackage.components.forEach { add(entity, it) }
  }
}
