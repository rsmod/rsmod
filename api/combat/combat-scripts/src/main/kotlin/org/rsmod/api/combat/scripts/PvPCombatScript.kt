package org.rsmod.api.combat.scripts

import jakarta.inject.Inject
import org.rsmod.api.combat.PvPCombat
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.manager.MagicRuneManager
import org.rsmod.api.combat.player.aggressiveNpc
import org.rsmod.api.combat.player.attackRange
import org.rsmod.api.combat.player.pkPredator1
import org.rsmod.api.combat.player.resolveAutocastSpell
import org.rsmod.api.combat.player.resolveCombatAttack
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.combat.weapon.types.AttackTypes
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.queues
import org.rsmod.api.player.isInPvpCombat
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.script.advanced.onApPlayer2
import org.rsmod.api.script.advanced.onOpPlayer2
import org.rsmod.api.script.onApPlayerT
import org.rsmod.api.spells.MagicSpellRegistry
import org.rsmod.api.spells.autocast.AutocastWeapons
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class PvPCombatScript
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val styles: AttackStyles,
    private val types: AttackTypes,
    private val combat: PvPCombat,
    private val spells: MagicSpellRegistry,
    private val runes: MagicRuneManager,
    private val autocast: AutocastWeapons,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onApPlayer2 { attemptCombatAp(it.target) }
        onOpPlayer2 { attemptCombatOp(it.target) }
        for (spell in spells.combatSpells()) {
            onApPlayerT(spell.component) { attemptCombatSpell(it.target, spell) }
        }
    }

    private suspend fun ProtectedAccess.attemptCombatAp(target: Player) {
        val type = types.get(player)
        val style = styles.get(player)
        val attackRange = attackRange(style)
        val canAttack = canAttack(target)

        // Weapons such as salamanders have an attack range of `1` but can attack with both ranged
        // and magic. These attacks should be treated as ap range, not op.
        val isMeleeAttackType = type == null || type.isMelee
        if (attackRange == 1 && isMeleeAttackType) {
            apRange(-1)
            return
        }

        if (!canAttack) {
            return
        }

        if (!isWithinDistance(target, attackRange)) {
            apRange(attackRange)
            return
        }

        val spell = resolveAutocastSpell(objTypes, spells, runes, autocast)
        val attack = resolveCombatAttack(player.righthand, type, style, spell)
        combat.attack(this, target, attack)
    }

    private suspend fun ProtectedAccess.attemptCombatOp(target: Player) {
        if (!canAttack(target)) {
            return
        }
        val type = types.get(player)
        val style = styles.get(player)

        val spell = resolveAutocastSpell(objTypes, spells, runes, autocast)
        val attack = resolveCombatAttack(player.righthand, type, style, spell)
        combat.attack(this, target, attack)
    }

    private suspend fun ProtectedAccess.attemptCombatSpell(target: Player, spell: MagicSpell) {
        val canCast = runes.canCastSpell(player, spell)
        if (!canCast) {
            return
        }
        // Official behavior: `canAttack` checks occur _after_ `canCastSpell` checks.
        val canAttack = canAttack(target)
        if (!canAttack) {
            return
        }
        // Note: Ap range condition is not necessary as magic spells can be cast from `10` tiles
        // away, which is the same as the default engine valid-ap range.
        val attack = resolveCombatAttack(player.righthand, null, null, spell)
        combat.attack(this, target, attack)
    }

    private fun ProtectedAccess.canAttack(target: Player): Boolean {
        val weapon = objTypes.getOrNull(player.righthand)
        if (weapon != null && weapon.isCategoryType(categories.dinhs_bulwark)) {
            val attackStyle = styles.get(player)
            // Dinh's "Block" attack style uses `AggressiveMelee` as its "dummy" attack style.
            if (attackStyle == AttackStyle.AggressiveMelee) {
                mes("Your bulwark gets in the way.")
                clearPendingAction()
                return false
            }
        }

        // Dinh's bulwark style-switching delay is added to a queue and is applied globally during
        // this condition check. This means even if you quickly change to another melee weapon and
        // re-interact with a target, you will _not_ move into op range.
        if (queues.dinhs_combat_delay in player.queueList) {
            clearPendingAction()
            return false
        }

        // TODO(combat): Updated multiway logic.
        // TODO(combat): Add singles plus support.
        val singleCombat = !mapMultiway()
        if (singleCombat) {
            if (isInCombat()) {
                if (pkPredator1 != null && pkPredator1 != target.uid) {
                    spam("I'm already under attack.")
                    return false
                }

                val aggressiveNpc = aggressiveNpc
                if (aggressiveNpc != null && findUid(aggressiveNpc) != null) {
                    spam("I'm already under attack.")
                    return false
                }
            }

            if (target.isInPvpCombat()) {
                if (target.pkPredator1 != null && target.pkPredator1 != player.uid) {
                    mes("${target.displayName} is fighting another player.")
                    return false
                }
            }
        }
        return true
    }
}
