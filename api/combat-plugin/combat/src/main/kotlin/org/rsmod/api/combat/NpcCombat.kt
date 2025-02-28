package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.fx.MeleeAnimationAndSound
import org.rsmod.api.combat.player.canPerformMeleeSpecial
import org.rsmod.api.combat.player.canPerformShieldSpecial
import org.rsmod.api.combat.player.specialAttackType
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.SpecialAttackRegistry
import org.rsmod.api.specials.SpecialAttackType
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.game.entity.Npc
import org.rsmod.game.interact.InteractionOp

internal class NpcCombat
@Inject
constructor(
    private val speeds: WeaponSpeeds,
    private val specialsReg: SpecialAttackRegistry,
    private val specialEnergy: SpecialAttackEnergy,
) {
    suspend fun attack(access: ProtectedAccess, target: Npc, attack: CombatAttack) {
        println("attack with $attack")
        when (attack) {
            is CombatAttack.Melee -> access.attackMelee(target, attack)
            is CombatAttack.Ranged -> access.attackRanged(target, attack)
            is CombatAttack.Spell -> access.attackMagicSpell(target, attack)
            is CombatAttack.Staff -> access.attackMagicStaff(target, attack)
        }
    }

    private suspend fun ProtectedAccess.attackMelee(npc: Npc, attack: CombatAttack.Melee) {
        if (!canAttack(npc)) {
            stopAction()
            return
        }

        if (actionDelay > mapClock) {
            opNpc2(npc)
            return
        }

        // As a quality-of-life feature, set the next attack clock _before_ performing special
        // attacks. This ensures that, by default, specials do not need to handle attack timing
        // themselves, while still allowing them to override it if necessary.
        val attackRate = speeds.actual(player)
        actionDelay = mapClock + attackRate

        if (specialAttackType == SpecialAttackType.Weapon) {
            specialAttackType = SpecialAttackType.None

            val activatedSpec = canPerformMeleeSpecial(npc, attack, specialsReg, specialEnergy)
            if (activatedSpec) {
                opNpc2(npc)
                return
            }
        }

        if (specialAttackType == SpecialAttackType.Shield) {
            specialAttackType = SpecialAttackType.None

            val shield = player.lefthand
            val activatedSpec = canPerformShieldSpecial(npc, shield, specialsReg)
            if (activatedSpec) {
                opNpc2(npc)
                return
            }
        }

        val (weapon, type, style, stance) = attack

        val animAndSound = MeleeAnimationAndSound.from(stance)
        val (animParam, soundParam, defaultAnim, defaultSound) = animAndSound

        val attackAnim = ocParamOrNull(weapon, animParam) ?: defaultAnim
        val attackSound = ocParamOrNull(weapon, soundParam) ?: defaultSound

        anim(attackAnim)
        soundSynth(attackSound)

        // val defendSound = npcParam(target, params.defend_sound) // TODO
        val defendAnim = npcParamOrNull(npc, params.defend_anim)
        if (defendAnim != null) {
            npc.anim(defendAnim)
        }

        npc.playerFaceClose(player) // TODO: Replace with npc retaliate

        opNpc2(npc)
    }

    private suspend fun ProtectedAccess.attackRanged(target: Npc, attack: CombatAttack.Ranged) {
        TODO()
    }

    private suspend fun ProtectedAccess.attackMagicSpell(target: Npc, attack: CombatAttack.Spell) {
        TODO()
    }

    private suspend fun ProtectedAccess.attackMagicStaff(target: Npc, attack: CombatAttack.Staff) {
        TODO()
    }

    private fun ProtectedAccess.canAttack(npc: Npc): Boolean {
        if (!npc.isValidTarget) {
            return false
        }

        val hasAttackOp = npc.visType.hasOp(InteractionOp.Op2)
        if (!hasAttackOp) {
            mes("You can't attack this npc.")
            return false
        }

        return true
    }
}
