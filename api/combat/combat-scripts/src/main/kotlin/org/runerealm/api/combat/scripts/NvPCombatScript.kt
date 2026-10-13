package org.runerealm.api.combat.scripts

import jakarta.inject.Inject
import org.runerealm.api.area.checker.AreaChecker
import org.runerealm.api.combat.NvPCombat
import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.combat.player.aggressiveNpc
import org.runerealm.api.config.refs.categories
import org.runerealm.api.config.refs.params
import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.api.player.isInPvnCombat
import org.runerealm.api.player.isInPvpCombat
import org.runerealm.api.script.advanced.onDefaultAiOpPlayer2
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.type.category.isType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

internal class NvPCombatScript
@Inject
constructor(private val combat: NvPCombat, private val areaChecker: AreaChecker) : PluginScript() {
    override fun ScriptContext.startup() {
        onDefaultAiOpPlayer2 { attemptCombatOp(it.target) }
    }

    private fun StandardNpcAccess.attemptCombatOp(target: Player) {
        if (!canAttack(target)) {
            resetMode()
            return
        }
        val attackType = npc.resolveMeleeAttackType()
        val attack = CombatAttack.NpcMelee(attackType)
        combat.attack(this, target, attack)
    }

    private fun StandardNpcAccess.canAttack(target: Player): Boolean {
        val singleCombat = !mapMultiway(areaChecker)
        if (singleCombat) {
            if (target.isInPvpCombat()) {
                return false
            }

            if (target.isInPvnCombat()) {
                if (target.aggressiveNpc != null && target.aggressiveNpc != npc.uid) {
                    return false
                }
            }
        }
        return true
    }

    private fun Npc.resolveMeleeAttackType(): MeleeAttackType {
        val category = visType.paramOrNull(params.npc_attack_type)
        return when {
            category.isType(categories.attacktype_stab) -> MeleeAttackType.Stab
            category.isType(categories.attacktype_slash) -> MeleeAttackType.Slash
            else -> MeleeAttackType.Crush
        }
    }
}
