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
import java.lang.reflect.ParameterizedType
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun engine(builder: EngineBuilder.() -> Unit): Engine = EngineBuilder().apply(builder).build()

inline fun <reified T> key() = object : Key<T>() {}

data class SystemKeys<EventType : Event, SystemType : System<EventType>>(
  val systemKey: Key<out SystemType>
) {
  @Suppress("UNCHECKED_CAST") // System has one Event type parameter
  val eventKey: Key<out EventType> by lazy {
    Key.get(
      systemKey.typeLiteral.rawType.genericInterfaces
        .map { it as ParameterizedType }
        .single { it.rawType == System::class.java }
        .actualTypeArguments[0]
    ) as Key<out EventType>
  }
}

class EngineBuilder {
  private val entityComponentStore = NomoEntityComponentStore()
  private val eventStore = NomoEventStore()

  val systemKeys: MutableSet<SystemKeys<Event, System<Event>>> = mutableSetOf()

  inline fun <reified SystemType : System<*>> add() {
    systemKeys.add(
      @Suppress("UNCHECKED_CAST") // System has one Event type parameter
      SystemKeys(key<SystemType>() as Key<System<Event>>)
    )
  }

  internal fun build(): Engine {
    val injector =
      Guice.createInjector(
        object : AbstractModule() {
          override fun configure() {
            systemKeys.forEach { bind(it.systemKey).asEagerSingleton() }
          }

          @Provides
          @Singleton
          private fun providesEngine(injector: Injector): BasicEngine =
            BasicEngine(injector, systemKeys, eventStore, entityComponentStore)
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
  private val systemKeySet: Set<SystemKeys<Event, System<Event>>>,
  eventStore: EventStore,
  entityComponentStore: EntityComponentStore,
) : Engine, EventStore by eventStore, EntityComponentStore by entityComponentStore {
  private val eventScope = CoroutineScope(Dispatchers.Default)

  override suspend fun start() {
    systemKeySet.forEach { it ->
      eventScope.launch {
        val flow = flowFor(it.eventKey)
        val system = injector.getInstance(it.systemKey)
        flow.collect(system::handle)
      }
    }
    dispatchEvent(StartEvent)
  }

  @ExperimentalTime
  override suspend fun update(elapsed: Duration) {
    dispatchEvent(UpdateEvent(elapsed))
  }
}
