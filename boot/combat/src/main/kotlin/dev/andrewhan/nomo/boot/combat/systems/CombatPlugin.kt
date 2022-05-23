package dev.andrewhan.nomo.boot.combat.systems

import dev.andrewhan.nomo.boot.combat.events.DamageEvent
import dev.andrewhan.nomo.boot.combat.events.DeathEvent
import dev.andrewhan.nomo.sdk.engines.EnginePlugin

val CombatPlugin: EnginePlugin = {
  forEvent<DamageEvent> { run<ShieldSystem>() then run<ArmorSystem>() then run<DamageSystem>() }
  forEvent<DeathEvent> { run<RemoveOnDeathSystem>() }
}
