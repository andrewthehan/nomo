package dev.andrewhan.nomo.example.items.xp

import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant

data class ExperienceItemComponent(val amount: Int) : Component, Pendant, Exclusive
