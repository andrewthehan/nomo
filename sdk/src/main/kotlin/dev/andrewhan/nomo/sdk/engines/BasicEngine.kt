package dev.andrewhan.nomo.sdk.engines

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Provides
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import dev.andrewhan.nomo.sdk.events.StartEvent
import dev.andrewhan.nomo.sdk.events.UpdateEvent
import dev.andrewhan.nomo.sdk.stores.EntityComponentStore
import dev.andrewhan.nomo.sdk.stores.EventStore
import dev.andrewhan.nomo.sdk.stores.NomoEntityComponentStore
import dev.andrewhan.nomo.sdk.stores.NomoEventStore
import dev.andrewhan.nomo.sdk.util.DirectedGraph
import dev.andrewhan.nomo.sdk.util.getTopologicalSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

fun basicEngine(builder: BasicEngineBuilder.() -> Unit): NomoEngine =
  BasicEngineBuilder().apply(builder).build()

class BasicEngineBuilder {
  private val entityComponentStore = NomoEntityComponentStore()
  private val eventStore = NomoEventStore()

  val systemOrder: DirectedGraph<SystemMetadata<Event, System<Event>>> = DirectedGraph()
  val systemMetadataMap: MutableMap<Key<System<Event>>, SystemMetadata<Event, System<Event>>> =
    mutableMapOf()

  inline fun <reified SystemType : System<*>> add() {
    val systemKey = systemKey<SystemType>()
    val systemMetadata = SystemMetadata(systemKey)
    systemOrder.addNode(systemMetadata)
    systemMetadataMap[systemKey] = systemMetadata
  }

  inline fun <
    EventType : Event, reified A : System<EventType>, reified B : System<EventType>> order() {
    systemOrder.addEdge(systemMetadataMap[systemKey<A>()]!!, systemMetadataMap[systemKey<B>()]!!)
  }

  internal fun build(): NomoEngine {
    val injector =
      Guice.createInjector(
        object : AbstractModule() {
          override fun configure() {
            systemMetadataMap.values.forEach { bind(it.systemKey).asEagerSingleton() }
          }

          @Provides
          @Singleton
          private fun providesEngine(injector: Injector): NomoEngine =
            BasicEngine(injector, systemOrder, eventStore, entityComponentStore)
        }
      )
    return injector.getInstance(key<NomoEngine>())
  }
}

private class BasicEngine(
  private val injector: Injector,
  private val systemOrder: DirectedGraph<SystemMetadata<Event, System<Event>>>,
  eventStore: EventStore,
  entityComponentStore: EntityComponentStore,
) : NomoEngine, EventStore by eventStore, EntityComponentStore by entityComponentStore {
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

  override suspend fun stop() {
    eventScope.cancel()
  }

  @ExperimentalTime
  override suspend fun update(elapsed: Duration) {
    dispatchEvent(UpdateEvent(elapsed))
  }
}
