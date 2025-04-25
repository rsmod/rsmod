package org.rsmod.api.npc.plugin

import kotlin.math.min
import kotlin.math.round
import org.rsmod.api.config.refs.params
import org.rsmod.api.script.onEvent
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.npc.NpcStateEvents
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class AiSpawnScript : PluginScript() {
    override fun ScriptContext.startup() {
        // Note: This behavior changed at some point - it previously used `ai_spawn` events to set
        // npc combat xp multipliers, which introduced a one-cycle delay before the multiplier was
        // applied. This behavior was eventually abused and subsequently patched. It's unclear
        // exactly how it was fixed. Here, we simply use an `npc_spawn`-equivalent event to assign
        // the multiplier.
        onEvent<NpcStateEvents.Create> { npc.assignCombatXpMultiplier() }
    }

    private fun Npc.assignCombatXpMultiplier() {
        combatXpMultiplier = resolveCombatXpMultiplier()
    }

    private fun Npc.resolveCombatXpMultiplier(): Int {
        val presetMultiplier = type.paramOrNull(params.npc_com_xp_multiplier)
        if (presetMultiplier != null) {
            return presetMultiplier
        }
        val averageLevel = averageLevel()
        val averageDefBonus = averageDefBonus()
        val attackBonus = type.param(params.attack_melee)
        val strengthBonus = type.param(params.melee_strength)
        val scaledVariables =
            (averageLevel * (averageDefBonus + strengthBonus + attackBonus)) / 5120
        val multiplier = 1 + (0.025 * scaledVariables)
        return round(multiplier * 1000.0).toInt()
    }

    private fun Npc.averageLevel(): Int {
        val cappedHitpoints = min(2000, baseHitpointsLvl)
        return (baseAttackLvl + baseStrengthLvl + baseDefenceLvl + cappedHitpoints) / 4
    }

    private fun Npc.averageDefBonus(): Int {
        val stabDefence = type.param(params.defence_stab)
        val slashDefence = type.param(params.defence_slash)
        val crushDefence = type.param(params.defence_crush)
        return (stabDefence + slashDefence + crushDefence) / 3
    }
}
