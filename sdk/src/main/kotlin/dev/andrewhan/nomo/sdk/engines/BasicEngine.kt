package dev.andrewhan.nomo.sdk.engines

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.sdk.stores.EntityComponentStore
import dev.andrewhan.nomo.sdk.stores.EventStore
import dev.andrewhan.nomo.sdk.stores.NomoEntityComponentStore
import dev.andrewhan.nomo.sdk.stores.NomoEventStore
import dev.andrewhan.nomo.sdk.systems.NomoSystem
import dev.andrewhan.nomo.sdk.util.DirectedGraph
import dev.andrewhan.nomo.sdk.util.getTopologicalSort
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

private typealias NonGenericSystemKey = Key<NomoSystem<Event>>

private typealias NonGenericSystem = NomoSystem<Event>

private typealias NonGenericSystemMetadata = SystemMetadata<Event, NomoSystem<Event>>

@Suppress("UNCHECKED_CAST")
fun <EventType : Event, SystemType : NomoSystem<EventType>, KeyType : Key<SystemType>> KeyType
  .eraseGenericType(): NonGenericSystemKey = this as NonGenericSystemKey

@Suppress("UNCHECKED_CAST")
fun <
  EventType : Event,
  SystemType : NomoSystem<EventType>,
  SystemMetadataType : SystemMetadata<EventType, SystemType>
> SystemMetadataType.eraseGenericType(): NonGenericSystemMetadata = this as NonGenericSystemMetadata

fun basicEngine(
  defaultScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
  builder: BasicEngineBuilder.() -> Unit
): NomoEngine {
  return BasicEngineBuilder(defaultScope).apply(builder).build()
}

class BasicEngineBuilder(val defaultScope: CoroutineScope) {
  val systemOrder: DirectedGraph<NonGenericSystemMetadata> = DirectedGraph()
  val systemMetadataMap: MutableMap<Key<NonGenericSystem>, NonGenericSystemMetadata> =
    mutableMapOf()

  val constantBindings: MutableMap<Key<Any>, Any> = mutableMapOf()

  class EventSubscriptionBuilder<EventType : Event>(
    val systemOrder: DirectedGraph<NonGenericSystemMetadata>,
    val systemMetadataMap: MutableMap<Key<NonGenericSystem>, NonGenericSystemMetadata>,
    val scope: CoroutineScope
  ) {
    inline fun <reified SystemType : NomoSystem<EventType>> run():
      SystemMetadata<EventType, SystemType> {
      val systemKey = systemKey<SystemType>()
      val metadata = SystemMetadata(systemKey, scope)

      systemOrder.addNode(metadata.eraseGenericType())
      systemMetadataMap[systemKey.eraseGenericType()] = metadata.eraseGenericType()

      return metadata
    }

    infix fun <
      SystemA : NomoSystem<EventType>,
      SystemB : NomoSystem<EventType>,
      A : SystemMetadata<EventType, SystemA>,
      B : SystemMetadata<EventType, SystemB>
    > A.then(other: B): B {
      systemOrder.addEdge(this.eraseGenericType(), other.eraseGenericType())
      return other
    }
  }

  inline fun <reified EventType : Event> forEvent(
    scope: CoroutineScope = defaultScope,
    builder: EventSubscriptionBuilder<EventType>.() -> Unit
  ) {
    EventSubscriptionBuilder<EventType>(systemOrder, systemMetadataMap, scope).apply(builder)
  }

  inline fun <reified AnnotationType : Annotation, reified T> constant(valueProvider: () -> T) {
    @Suppress("UNCHECKED_CAST") // T is Any
    val key = key<T>(AnnotationType::class) as Key<Any>
    check(!constantBindings.contains(key)) {
      "A constant is already bound to @${AnnotationType::class.simpleName} ${T::class.simpleName}."
    }
    constantBindings[key] = valueProvider() as Any
  }

  fun build(): NomoEngine {
    val injector =
      Guice.createInjector(
        object : AbstractModule() {
          override fun configure() {
            systemMetadataMap.values.forEach { bind(it.systemKey).asEagerSingleton() }
            constantBindings.forEach { bind(it.key).toInstance(it.value) }

            bind(key<DirectedGraph<NonGenericSystemMetadata>>()).toInstance(systemOrder)
            bind(key<CoroutineScope>(EngineCoroutineScope::class)).toInstance(defaultScope)

            bind(key<NomoEngine>()).to(key<BaseEngine>()).asEagerSingleton()
          }
        }
      )
    return injector.getInstance(key<NomoEngine>())
  }
}

class BaseEngine
@Inject
constructor(
  private val injector: Injector,
  @EngineCoroutineScope private val scope: CoroutineScope,
  private val systemOrder: DirectedGraph<NonGenericSystemMetadata>
) : NomoEngine, EventStore by NomoEventStore(), EntityComponentStore by NomoEntityComponentStore() {
  override suspend fun start() {
    val totalSubscriptionCount =
      systemOrder.nodes
        .map { systemOrder.getIncomingEdges(it) }
        .sumOf { if (it.isNotEmpty()) it.size else 1 }
    val latch = CountDownLatch(totalSubscriptionCount)

    systemOrder.nodes
      .map { injector.getInstance(it.systemKey) }
      .forEach { system ->
        system.subscriptionCount.filter { it > 0 }.onEach { latch.countDown() }.launchIn(scope)
      }
    subscriptionCount
      .filter { it > 0 }
      .onEach { latch.countDown() }
      .launchIn(scope)

    systemOrder.getTopologicalSort().forEach { metadata ->
      val system = injector.getInstance(metadata.systemKey)
      val inputSystems =
        systemOrder.getIncomingEdges(metadata).map { injector.getInstance(it.systemKey) }
      val flow: Flow<Event> =
        if (inputSystems.isNotEmpty()) {
          inputSystems.map { it.events }.merge()
        } else {
          flowFor(metadata.eventKey)
        }

      system.subscribe(metadata.scope, flow)
    }

    withContext(scope.coroutineContext) { latch.await() }

    systemOrder.nodes.map { injector.getInstance(it.systemKey) }.forEach { it.start() }
  }
}
