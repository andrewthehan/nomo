package dev.andrewhan.nomo.sdk.stores

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.core.Entity
import dev.andrewhan.nomo.core.Store
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.ExclusiveException
import dev.andrewhan.nomo.sdk.components.Pendant
import dev.andrewhan.nomo.sdk.components.PendantException
import dev.andrewhan.nomo.sdk.util.IdentityBiMultiMap
import dev.andrewhan.nomo.sdk.util.IdentityMultiMap
import dev.andrewhan.nomo.sdk.util.toIdentitySet
import kotlin.reflect.KClass

inline fun <reified ComponentType : Component> EntityComponentStore.getComponents():
  Set<ComponentType> = getComponents(ComponentType::class)

inline fun <reified ComponentType : Component> EntityComponentStore.getAssignableComponents():
  Set<ComponentType> = getAssignableComponents(ComponentType::class)

inline fun <reified ComponentType : Component> EntityComponentStore.getComponents(
  entity: Entity
): Set<ComponentType> = this[entity].filterIsInstance<ComponentType>().toIdentitySet()

inline fun <reified ComponentType : Component> EntityComponentStore.getAssignableComponents(
  entity: Entity
): Set<ComponentType> = getAssignableComponents(ComponentType::class, entity)

inline fun <reified ComponentType : Component> EntityComponentStore.getEntities(): Set<Entity> =
  getEntities(ComponentType::class)

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

inline fun <reified ExclusiveComponent> EntityComponentStore.getAssignableComponent(
  entity: Entity
): ExclusiveComponent where ExclusiveComponent : Component, ExclusiveComponent : Exclusive =
  getAssignableComponents<ExclusiveComponent>(entity).single()

inline fun <reified ExclusiveComponent> EntityComponentStore.getComponentOrNull(
  entity: Entity
): ExclusiveComponent? where ExclusiveComponent : Component, ExclusiveComponent : Exclusive =
  this[entity].filterIsInstance<ExclusiveComponent>().singleOrNull()

inline fun <reified ComponentType : Component> EntityComponentStore.containsComponent(
  entity: Entity
): Boolean = getComponents<ComponentType>(entity).isNotEmpty()

interface EntityComponentStore : Store {
  val entities: Set<Entity>
  val components: Set<Component>

  fun add(entity: Entity, component: Component)

  operator fun get(entity: Entity): Set<Component>

  operator fun get(component: Component): Set<Entity>

  fun <ComponentType : Component> getComponents(
    componentType: KClass<ComponentType>
  ): Set<ComponentType>

  fun <ComponentType : Component> getAssignableComponents(
    componentType: KClass<ComponentType>
  ): Set<ComponentType>

  fun <ComponentType : Component> getAssignableComponents(
    componentType: KClass<ComponentType>,
    entity: Entity
  ): Set<ComponentType>

  fun <ComponentType : Component> getEntities(componentType: KClass<ComponentType>): Set<Entity>

  fun contains(entity: Entity): Boolean

  fun contains(component: Component): Boolean

  fun remove(entity: Entity, component: Component): Boolean

  fun remove(entity: Entity): Set<Component>

  fun remove(component: Component): Set<Entity>
}

internal class NomoEntityComponentStore : EntityComponentStore {
  private val entitiesToComponentsMap = IdentityBiMultiMap<Entity, Component>()
  private val componentTypeToComponentsMap = IdentityMultiMap<KClass<out Component>, Component>()

  override val entities: Set<Entity>
    get() = entitiesToComponentsMap.getKeys()
  override val components: Set<Component>
    get() = entitiesToComponentsMap.getValues()

  override fun add(entity: Entity, component: Component) {
    synchronized(this) {
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
      componentTypeToComponentsMap.put(component::class, component)
    }
  }

  override operator fun get(entity: Entity) = entitiesToComponentsMap[entity]

  override operator fun get(component: Component) = entitiesToComponentsMap.getByValue(component)

  override fun <ComponentType : Component> getComponents(
    componentType: KClass<ComponentType>
  ): Set<ComponentType> {
    @Suppress("UNCHECKED_CAST") // map is configured to return the right type
    return componentTypeToComponentsMap[componentType] as Set<ComponentType>
  }

  override fun <ComponentType : Component> getAssignableComponents(
    componentType: KClass<ComponentType>
  ): Set<ComponentType> {
    return synchronized(this) {
      @Suppress("UNCHECKED_CAST") // map is configured to return the right type
      componentTypeToComponentsMap.keys
        .filter { componentType.java.isAssignableFrom(it.java) }
        .flatMap { componentTypeToComponentsMap[it] }
        .toIdentitySet() as Set<ComponentType>
    }
  }

  override fun <ComponentType : Component> getAssignableComponents(
    componentType: KClass<ComponentType>,
    entity: Entity
  ): Set<ComponentType> {
    return synchronized(this) {
      @Suppress("UNCHECKED_CAST") // checked by filter
      this[entity].filter { componentType.java.isAssignableFrom(it::class.java) }.toIdentitySet()
        as Set<ComponentType>
    }
  }

  override fun <ComponentType : Component> getEntities(
    componentType: KClass<ComponentType>
  ): Set<Entity> = getComponents(componentType).map(this::get).flatten().distinct().toSet()

  override fun contains(entity: Entity): Boolean = entitiesToComponentsMap.containsKey(entity)

  override fun contains(component: Component): Boolean =
    entitiesToComponentsMap.containsValue(component)

  override fun remove(entity: Entity, component: Component): Boolean {
    return synchronized(this) {
      entitiesToComponentsMap.remove(entity, component).also { removed ->
        if (removed) {
          if (!contains(component)) {
            assert(componentTypeToComponentsMap.remove(component::class, component)) {
              "$component should exist in componentTypeToComponentsMap but did not."
            }
          }
        }
      }
    }
  }

  override fun remove(entity: Entity): Set<Component> {
    return synchronized(this) {
      entitiesToComponentsMap.removeKey(entity).also { associatedComponents ->
        assert(
          associatedComponents
            .filter { component -> !contains(component) }
            .all { component -> componentTypeToComponentsMap.remove(component::class, component) }
        ) {
          "All components removed from entitiesToComponentsMap should exist in componentTypeToComponentsMap but do not."
        }
      }
    }
  }

  override fun remove(component: Component): Set<Entity> {
    return synchronized(this) {
      entitiesToComponentsMap.removeValue(component).also { associatedEntities ->
        if (associatedEntities.isNotEmpty()) {
          assert(componentTypeToComponentsMap.remove(component::class, component)) {
            "$component should exist in componentTypeToComponentsMap but did not."
          }
        }
      }
    }
  }
}
