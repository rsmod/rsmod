package org.rsmod.api.combat.scripts

import jakarta.inject.Inject
import org.rsmod.api.combat.ACTIVE_COMBAT_DELAY
import org.rsmod.api.combat.PlayerCombat
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.CombatStance
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.inMultiCombatArea
import org.rsmod.api.combat.player.aggressiveNpc
import org.rsmod.api.combat.player.lastCombat
import org.rsmod.api.combat.player.lastCombatPvp
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.script.advanced.onDefaultAiOpPlayer2
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class PlayerTargetScript @Inject constructor(private val combat: PlayerCombat) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onDefaultAiOpPlayer2 { attemptCombatOp(it.target) }
    }

    private fun StandardNpcAccess.attemptCombatOp(target: Player) {
        if (!canAttack(target)) {
            resetMode()
            return
        }

        // TODO(combat): What should the defaults be? Is it worth separating npc attacks into their
        //  own [CombatAttack]s?
        val attack =
            CombatAttack.Melee(
                weapon = null,
                MeleeAttackType.Slash,
                MeleeAttackStyle.Controlled,
                CombatStance.Stance1,
            )

        persistentInteraction()
        combat.attack(this, target, attack)
    }

    private fun StandardNpcAccess.canAttack(target: Player): Boolean {
        val singleCombat = !inMultiCombatArea()
        if (singleCombat) {
            if (target.lastCombatPvp + ACTIVE_COMBAT_DELAY > mapClock) {
                return false
            }

            if (target.lastCombat + ACTIVE_COMBAT_DELAY > mapClock) {
                if (target.aggressiveNpc != null && target.aggressiveNpc != npc.uid) {
                    return false
                }
            }
        }
        return true
    }
}
