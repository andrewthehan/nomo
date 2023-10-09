package dev.andrewhan.nomo.integration.libgdx.render.components

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant

sealed interface CameraFollowComponent : Component, Pendant, Exclusive

class ExactCameraFollowComponent : CameraFollowComponent

class InverseQuadraticCameraFollowComponent(val speed: Float) : CameraFollowComponent
