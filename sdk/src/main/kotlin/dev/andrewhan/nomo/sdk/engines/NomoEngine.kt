package dev.andrewhan.nomo.sdk.engines

import dev.andrewhan.nomo.core.Engine
import dev.andrewhan.nomo.sdk.stores.EntityComponentStore
import dev.andrewhan.nomo.sdk.stores.EventStore

interface NomoEngine : Engine, EntityComponentStore, EventStore