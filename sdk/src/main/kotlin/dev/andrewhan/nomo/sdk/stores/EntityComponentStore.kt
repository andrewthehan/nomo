package dev.andrewhan.nomo.sdk.stores

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Store
import dev.andrewhan.nomo.sdk.exceptions.ExclusiveException
import dev.andrewhan.nomo.sdk.exceptions.PendantException
import dev.andrewhan.nomo.sdk.interfaces.Exclusive
import dev.andrewhan.nomo.sdk.interfaces.Pendant
import dev.andrewhan.nomo.sdk.util.IdentityBiMultiMap

inline fun <reified ComponentType : Component> EntityComponentStore.getComponents(entity: Entity) =
  this[entity].filterIsInstance<ComponentType>()

inline fun <reified ComponentType : Component> EntityComponentStore.getComponents() =
  components.filterIsInstance<ComponentType>()

inline fun <reified ComponentType : Component> EntityComponentStore.getEntities() =
  getComponents<ComponentType>().map(this::get).flatten().distinct()

fun <PendantComponent> EntityComponentStore.getEntity(component: PendantComponent): Entity where
PendantComponent : Component,
PendantComponent : Pendant = this[component].single()

fun <PendantComponent> EntityComponentStore.getEntityOrNull(
  component: PendantComponent
): Entity? where PendantComponent : Component, PendantComponent : Pendant =
  this[component].singleOrNull()

inline fun <reified ExclusiveComponent> EntityComponentStore.getComponent(
  entity: Entity
): ExclusiveComponent where ExclusiveComponent : Component, ExclusiveComponent : Exclusive =
  this[entity].filterIsInstance<ExclusiveComponent>().single()

inline fun <reified ExclusiveComponent> EntityComponentStore.getComponentOrNull(
  entity: Entity
): ExclusiveComponent? where ExclusiveComponent : Component, ExclusiveComponent : Exclusive =
  this[entity].filterIsInstance<ExclusiveComponent>().singleOrNull()

interface EntityComponentStore : Store {
  val entities: Set<Entity>
  val components: Set<Component>

  fun add(entity: Entity, component: Component)

  operator fun get(entity: Entity): Set<Component>

  operator fun get(component: Component): Set<Entity>

  fun remove(entity: Entity, component: Component): Boolean

  fun remove(entity: Entity): Set<Component>

  fun remove(component: Component): Set<Entity>
}

internal class NomoEntityComponentStore : EntityComponentStore {
  private val entitiesToComponentsMap = IdentityBiMultiMap<Entity, Component>()

  override val entities: Set<Entity>
    get() = entitiesToComponentsMap.getKeys()
  override val components: Set<Component>
    get() = entitiesToComponentsMap.getValues()

  override fun add(entity: Entity, component: Component) {
    if (component is Exclusive) {
      val componentTypeAlreadyBound =
        entitiesToComponentsMap[entity]
          .filter { it is Exclusive }
          .any { component::class.isInstance(it) || it::class.isInstance(component) }
      if (componentTypeAlreadyBound) {
        throw ExclusiveException(entity, component::class)
      }
    }
    if (component is Pendant) {
      val boundedEntities = entitiesToComponentsMap.getByValue(component)
      if (boundedEntities.any()) {
        throw PendantException(component, boundedEntities)
      }
    }

    entitiesToComponentsMap.put(entity, component)
  }

  override operator fun get(entity: Entity) = entitiesToComponentsMap[entity]

  override operator fun get(component: Component) = entitiesToComponentsMap.getByValue(component)

  override fun remove(entity: Entity, component: Component) =
    entitiesToComponentsMap.remove(entity, component)

  override fun remove(entity: Entity) = entitiesToComponentsMap.removeKey(entity)

  override fun remove(component: Component) = entitiesToComponentsMap.removeValue(component)
}
