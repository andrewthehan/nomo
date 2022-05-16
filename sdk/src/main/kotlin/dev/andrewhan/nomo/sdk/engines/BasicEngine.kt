package dev.andrewhan.nomo.sdk.engines

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Provides
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.core.System
import dev.andrewhan.nomo.sdk.events.RenderEvent
import dev.andrewhan.nomo.sdk.events.StartEvent
import dev.andrewhan.nomo.sdk.stores.EntityComponentStore
import dev.andrewhan.nomo.sdk.stores.EventStore
import dev.andrewhan.nomo.sdk.stores.NomoEntityComponentStore
import dev.andrewhan.nomo.sdk.stores.NomoEventStore
import dev.andrewhan.nomo.sdk.util.DirectedGraph
import dev.andrewhan.nomo.sdk.util.getTopologicalSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import javax.inject.Singleton
import kotlin.reflect.KClass

fun basicEngine(builder: BasicEngineBuilder.() -> Unit): NomoEngine =
  BasicEngineBuilder().apply(builder).build()

class BasicEngineBuilder internal constructor() : AbstractModule(), EngineBuilder {
  val systemOrder: DirectedGraph<SystemMetadata<Event, System<Event>>> = DirectedGraph()
  val systemMetadataMap: MutableMap<Key<System<Event>>, SystemMetadata<Event, System<Event>>> =
    mutableMapOf()

  val constantBindings: MutableMap<Key<Any>, Any> = mutableMapOf()

  override fun configure() {
    systemMetadataMap.values.forEach { bind(it.systemKey).asEagerSingleton() }
    constantBindings.forEach { bind(it.key).toInstance(it.value) }
  }

  @Provides
  @Singleton
  private fun providesEngine(injector: Injector): NomoEngine = BasicEngine(injector, systemOrder)

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

  inline fun <reified T> bindConstant(annotation: KClass<out Annotation>, constant: T) {
    @Suppress("UNCHECKED_CAST") // T is Any
    val key = key<T>(annotation) as Key<Any>
    check(!constantBindings.contains(key)) {
      "A constant is already bound to @${annotation.simpleName} ${T::class.simpleName}."
    }
    constantBindings[key] = constant as Any
  }

  override fun build(): NomoEngine {
    val injector = Guice.createInjector(this)
    return injector.getInstance(key<NomoEngine>())
  }
}

class BasicEngine
internal constructor(
  private val injector: Injector,
  private val systemOrder: DirectedGraph<SystemMetadata<Event, System<Event>>>
) : NomoEngine, EventStore by NomoEventStore(), EntityComponentStore by NomoEntityComponentStore() {
  override var state: EngineState = EngineState.STARTING

  override suspend fun start(updateScope: CoroutineScope, renderScope: CoroutineScope) {
    state = EngineState.RUNNING

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
      if (RenderEvent::class.java.isAssignableFrom(metadata.eventKey.typeLiteral.rawType)) {
        system.start(renderScope, flow)
      } else {
        system.start(updateScope, flow)
      }
    }
    dispatchEvent(StartEvent)
  }

  override suspend fun stop() {
    state = EngineState.STOPPED
  }
}
