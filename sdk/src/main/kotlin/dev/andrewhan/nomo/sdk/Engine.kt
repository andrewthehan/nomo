package dev.andrewhan.nomo.sdk

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Provides
import dev.andrewhan.nomo.core.Engine
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import dev.andrewhan.nomo.sdk.events.StartEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.EntityComponentStore
import dev.andrewhan.nomo.sdk.stores.EventStore
import dev.andrewhan.nomo.sdk.stores.NomoEntityComponentStore
import dev.andrewhan.nomo.sdk.stores.NomoEventStore
import dev.andrewhan.nomo.sdk.util.DirectedGraph
import dev.andrewhan.nomo.sdk.util.getAllAssignableTypes
import dev.andrewhan.nomo.sdk.util.getTopologicalSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

fun engine(builder: EngineBuilder.() -> Unit): Engine = EngineBuilder().apply(builder).build()

inline fun <reified T> key() = object : Key<T>() {}

data class SystemMetadata<EventType : Event, SystemType : System<EventType>>(
  val systemKey: Key<SystemType>
) {
  @Suppress("UNCHECKED_CAST") // System has one Event type parameter
  val eventKey: Key<EventType> by lazy {
    Key.get(
      systemKey.typeLiteral.rawType
        .getAllAssignableTypes()
        .asSequence()
        .filterIsInstance<ParameterizedType>()
        .filter { it.rawType is Class<*> }
        .filter { System::class.java.isAssignableFrom(it.rawType as Class<*>) }
        .map { it.actualTypeArguments[0] }
        .filterIsInstance<Class<*>>()
        .single()
    ) as Key<EventType>
  }
}

class EngineBuilder {
  private val entityComponentStore = NomoEntityComponentStore()
  private val eventStore = NomoEventStore()

  val systemOrder: DirectedGraph<SystemMetadata<Event, System<Event>>> = DirectedGraph()
  val systemMetadataMap: MutableMap<Key<System<Event>>, SystemMetadata<Event, System<Event>>> =
    mutableMapOf()

  inline fun <reified SystemType : System<*>> add() {
    val systemKey = key<SystemType>()
    @Suppress("UNCHECKED_CAST") // System has one Event type parameter
    val systemMetadata = SystemMetadata(systemKey as Key<System<Event>>)
    systemOrder.addNode(systemMetadata)
    systemMetadataMap[systemKey] = systemMetadata
  }

  inline fun <
    reified EventType : Event, reified A : System<EventType>, reified B : System<EventType>
  > order() {
    @Suppress("UNCHECKED_CAST") // System has one Event type parameter
    systemOrder.addEdge(
      systemMetadataMap[key<A>() as Key<System<Event>>]!!,
      systemMetadataMap[key<B>() as Key<System<Event>>]!!
    )
  }

  internal fun build(): Engine {
    val injector =
      Guice.createInjector(
        object : AbstractModule() {
          override fun configure() {
            systemMetadataMap.values.forEach { bind(it.systemKey).asEagerSingleton() }
          }

          @Provides
          @Singleton
          private fun providesEngine(injector: Injector): BasicEngine =
            BasicEngine(injector, systemOrder, eventStore, entityComponentStore)
        }
      )
    return injector.getInstance(key<BasicEngine>())
  }
}

private fun <EventType : Event> EventStore.flowFor(key: Key<EventType>): Flow<EventType> =
  flow()
    .filter { key.typeLiteral.rawType.isInstance(it) }
    .map {
      @Suppress("UNCHECKED_CAST") // checked using key above
      it as EventType
    }
    .buffer()

class BasicEngine(
  private val injector: Injector,
  private val systemOrder: DirectedGraph<SystemMetadata<Event, System<Event>>>,
  eventStore: EventStore,
  entityComponentStore: EntityComponentStore,
) : Engine, EventStore by eventStore, EntityComponentStore by entityComponentStore {
  private val eventScope = CoroutineScope(Dispatchers.Default)

  override suspend fun start() {
    systemOrder.getTopologicalSort().forEach { metadata ->
      val system = injector.getInstance(metadata.systemKey)
      val inputSystems =
        systemOrder.getIncomingEdges(metadata).map { injector.getInstance(it.systemKey) }
      val flow: Flow<Event> =
        if (inputSystems.isNotEmpty()) {
          inputSystems.map { it.flow() }.merge()
        } else {
          flowFor(metadata.eventKey)
        }
      eventScope.launch { system.start(flow) }
    }
    dispatchEvent(StartEvent)
  }

  @ExperimentalTime
  override suspend fun update(elapsed: Duration) {
    dispatchEvent(UpdateEvent(elapsed))
  }
}
