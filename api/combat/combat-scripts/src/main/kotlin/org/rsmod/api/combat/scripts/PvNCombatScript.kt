package org.rsmod.api.combat.scripts

import jakarta.inject.Inject
import org.rsmod.api.combat.ACTIVE_COMBAT_DELAY
import org.rsmod.api.combat.PvNCombat
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.manager.MagicRuneManager
import org.rsmod.api.combat.npc.aggressivePlayer
import org.rsmod.api.combat.npc.lastCombat
import org.rsmod.api.combat.player.aggressiveNpc
import org.rsmod.api.combat.player.attackRange
import org.rsmod.api.combat.player.resolveAutocastSpell
import org.rsmod.api.combat.player.resolveCombatAttack
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.combat.weapon.types.AttackTypes
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.queues
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.script.advanced.onDefaultApNpc2
import org.rsmod.api.script.advanced.onDefaultOpNpc2
import org.rsmod.api.script.onApNpcT
import org.rsmod.api.spells.MagicSpellRegistry
import org.rsmod.api.spells.autocast.AutocastWeapons
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class PvNCombatScript
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val styles: AttackStyles,
    private val types: AttackTypes,
    private val combat: PvNCombat,
    private val spells: MagicSpellRegistry,
    private val runes: MagicRuneManager,
    private val autocast: AutocastWeapons,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onDefaultApNpc2 { attemptCombatAp(it.npc) }
        onDefaultOpNpc2 { attemptCombatOp(it.npc) }
        for (spell in spells.combatSpells()) {
            onApNpcT(spell.component) { attemptCombatSpell(it.npc, spell) }
        }
    }

    private suspend fun ProtectedAccess.attemptCombatAp(target: Npc) {
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

    private suspend fun ProtectedAccess.attemptCombatOp(target: Npc) {
        if (!canAttack(target)) {
            return
        }
        val type = types.get(player)
        val style = styles.get(player)

        val spell = resolveAutocastSpell(objTypes, spells, runes, autocast)
        val attack = resolveCombatAttack(player.righthand, type, style, spell)
        combat.attack(this, target, attack)
    }

    private suspend fun ProtectedAccess.attemptCombatSpell(target: Npc, spell: MagicSpell) {
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

    private fun ProtectedAccess.canAttack(npc: Npc): Boolean {
        // TODO(combat): Handle "can attack hooks" here. Seems like the ones that give dialogues
        //  have a cool-down period, but it's more than likely something hardcoded into
        //  their specific conditions. Some npcs like the mage arena (wilderness) npcs
        //  don't give any sort of message, but simply won't allow players to melee them.
        //  (Will stop at ap range, doesn't drag you into melee range)

        // Note: Dinh's bulwark conditions occur _before_ multi-combat area checks and _after_
        // "can attack" hooks.
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

        val singleCombat = !mapMultiway()
        if (singleCombat) {
            if (isInPvpCombat()) {
                spam("I'm already under attack.")
                return false
            }

            if (isInPvnCombat()) {
                if (aggressiveNpc != null && aggressiveNpc != npc.uid) {
                    spam("I'm already under attack.")
                    return false
                }
            }

            // TODO: mes("playername is fighting another player.") for pvp.

            // TODO(combat): Support for npcs that only target a single player, such as barrows.
            if (npc.lastCombat + ACTIVE_COMBAT_DELAY > mapClock) {
                if (npc.aggressivePlayer != null && npc.aggressivePlayer != player.uid) {
                    mes("Someone else is fighting that.")
                    return false
                }
            }
        }

        return true
    }
}
