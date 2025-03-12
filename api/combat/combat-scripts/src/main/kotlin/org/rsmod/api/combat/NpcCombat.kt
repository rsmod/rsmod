package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.npc.combatPlayDefendFx
import org.rsmod.api.combat.commons.npc.queueCombatRetaliate
import org.rsmod.api.combat.formulas.MaxHitFormulae
import org.rsmod.api.combat.fx.MeleeAnimationAndSound
import org.rsmod.api.combat.player.canPerformMeleeSpecial
import org.rsmod.api.combat.player.canPerformShieldSpecial
import org.rsmod.api.combat.player.specialAttackType
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.queueHit
import org.rsmod.api.npc.isValidTarget
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.SpecialAttackRegistry
import org.rsmod.api.specials.SpecialAttackType
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.game.entity.Npc
import org.rsmod.game.hit.HitType
import org.rsmod.game.interact.InteractionOp

internal class NpcCombat
@Inject
constructor(
    private val speeds: WeaponSpeeds,
    private val specialsReg: SpecialAttackRegistry,
    private val specialEnergy: SpecialAttackEnergy,
    private val hitModifier: NpcHitModifier,
    private val maxHits: MaxHitFormulae,
) {
    suspend fun attack(access: ProtectedAccess, target: Npc, attack: CombatAttack) {
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
            return
        }

        // As a quality-of-life improvement for special attack scripts, set the next attack clock
        // before executing the special attack. This ensures that specials, by default, do not need
        // to handle attack timing themselves but can still override it if needed.
        val attackRate = speeds.actual(player)
        actionDelay = mapClock + attackRate

        if (specialAttackType == SpecialAttackType.Weapon) {
            specialAttackType = SpecialAttackType.None

            val activatedSpec = canPerformMeleeSpecial(npc, attack, specialsReg, specialEnergy)
            if (activatedSpec) {
                // Each special attack must call `opnpc2` to re-engage in combat. If this is
                // not done, the player will not continue attacking after the special, except
                // via auto-retaliation.
                return
            }
        }

        if (specialAttackType == SpecialAttackType.Shield) {
            specialAttackType = SpecialAttackType.None

            val shield = player.lefthand
            val activatedSpec = canPerformShieldSpecial(npc, shield, specialsReg)
            if (activatedSpec) {
                // Like weapon special attacks, shield special attacks must explicitly
                // call `opnpc2(target)` to continue combat.
                return
            }
        }

        // TODO(combat): "WeaponAttack" handling for specialized weapon attacks, such as Scythe of
        //  Vitur attacks which follows specific logic when performing a standard attack.

        val (weapon, type, style, stance) = attack

        val animAndSound = MeleeAnimationAndSound.from(stance)
        val (animParam, soundParam, defaultAnim, defaultSound) = animAndSound

        val attackAnim = ocParamOrNull(weapon, animParam) ?: defaultAnim
        val attackSound = ocParamOrNull(weapon, soundParam) ?: defaultSound

        anim(attackAnim)
        soundSynth(attackSound)

        // TODO(combat): Accuracy roll
        val maxHit = maxHits.getMeleeMaxHit(player, npc)
        val damage = random.of(0..maxHit)

        npc.queueHit(player, 1, HitType.Melee, damage, hitModifier)
        // TODO(combat): Add hero points. For emulation purposes, I'm pretty certain hero points
        //  are added here instead of during hit processing due to legacy/technical reasons.

        npc.combatPlayDefendFx(player)
        npc.queueCombatRetaliate(player)
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
        if (!npc.isValidTarget()) {
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
