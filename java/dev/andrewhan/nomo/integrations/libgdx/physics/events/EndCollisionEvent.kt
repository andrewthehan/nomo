package dev.andrewhan.nomo.integration.libgdx.physics.events

import com.badlogic.gdx.physics.box2d.Contact
import dev.andrewhan.nomo.core.Event
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld

data class EndCollisionEvent(val world: SafeWorld, val contact: Contact) : Event
