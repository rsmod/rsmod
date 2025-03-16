package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.npc.attackRate
import org.rsmod.api.combat.commons.player.combatPlayDefendFx
import org.rsmod.api.combat.commons.player.queueCombatRetaliate
import org.rsmod.api.combat.formulas.AccuracyFormulae
import org.rsmod.api.combat.formulas.MaxHitFormulae
import org.rsmod.api.config.refs.params
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.isValidTarget
import org.rsmod.api.player.output.soundSynth
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitType
import org.rsmod.game.type.obj.ObjTypeList

internal class NpcCombat
@Inject
constructor(
    private val accuracy: AccuracyFormulae,
    private val maxHits: MaxHitFormulae,
    private val objTypes: ObjTypeList,
) {
    fun attack(access: StandardNpcAccess, target: Player, attack: CombatAttack.NpcAttack) {
        when (attack) {
            is CombatAttack.NpcMelee -> access.attackMelee(target, attack)
            is CombatAttack.NpcRanged -> access.attackRanged(target, attack)
            is CombatAttack.NpcMagic -> access.attackMagic(target, attack)
        }
    }

    private fun StandardNpcAccess.attackMelee(target: Player, attack: CombatAttack.NpcMelee) {
        if (!canAttack(target)) {
            resetMode()
            return
        }

        // Note: We do not need to explicitly call `opplayer2` because npcs will automatically
        // repeat their last interaction until it is canceled (e.g., by changing their `npcmode`).
        if (actionDelay > mapClock) {
            return
        }

        val attackRate = npc.attackRate()
        actionDelay = mapClock + attackRate

        val attackAnim = npc.visType.param(params.attack_anim)
        val attackSound = npc.visType.paramOrNull(params.attack_sound)

        anim(attackAnim)
        attackSound?.let(target::soundSynth)

        val successfulHit = accuracy.rollMeleeAccuracy(npc, target, attack.type, random)

        val damage =
            if (successfulHit) {
                val maxHit = maxHits.getMeleeMaxHit(npc, target, attack.type)
                random.of(0..maxHit)
            } else {
                0
            }

        target.queueHit(npc, 1, HitType.Melee, damage)
        target.combatPlayDefendFx(damage, objTypes)
        target.queueCombatRetaliate(npc)
    }

    private fun StandardNpcAccess.attackRanged(target: Player, attack: CombatAttack.NpcRanged) {
        TODO()
    }

    private fun StandardNpcAccess.attackMagic(target: Player, attack: CombatAttack.NpcMagic) {
        TODO()
    }

    private fun canAttack(target: Player): Boolean {
        return target.isValidTarget()
    }
}
