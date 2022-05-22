package dev.andrewhan.nomo.boot.physics.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive

interface BodyComponent: Component, Exclusive

class StaticBodyComponent : BodyComponent

class KinematicBodyComponent : BodyComponent

class DynamicBodyComponent: BodyComponent