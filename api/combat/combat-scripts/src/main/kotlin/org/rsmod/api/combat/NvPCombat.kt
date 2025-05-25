package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.npc.attackRate
import org.rsmod.api.combat.commons.player.combatPlayDefendAnim
import org.rsmod.api.combat.commons.player.queueCombatRetaliate
import org.rsmod.api.combat.formulas.AccuracyFormulae
import org.rsmod.api.combat.formulas.MaxHitFormulae
import org.rsmod.api.combat.npc.attackingPlayer
import org.rsmod.api.combat.npc.lastAttack
import org.rsmod.api.combat.player.aggressiveNpc
import org.rsmod.api.combat.player.lastCombat
import org.rsmod.api.config.refs.params
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.isInCombat
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.isValidTarget
import org.rsmod.api.player.output.soundSynth
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitType
import org.rsmod.game.type.obj.ObjTypeList

internal class NvPCombat
@Inject
constructor(
    private val accuracy: AccuracyFormulae,
    private val maxHits: MaxHitFormulae,
    private val objTypes: ObjTypeList,
) {
    fun attack(access: StandardNpcAccess, target: Player, attack: CombatAttack.NpcMelee) {
        access.attackMelee(target, attack)
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

        if (!npc.isInCombat()) {
            resetMode()
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

        setAttackVars(target)

        // Note: Retaliation must be queued _before_ the hit. If queued after, every hit would
        // trigger the "speed-up" death mechanic, since the hit queues would no longer be the
        // last entries in the queue list at the time of processing.
        target.queueCombatRetaliate(npc)

        target.queueHit(npc, 1, HitType.Melee, damage)
        target.combatPlayDefendAnim(objTypes)
    }

    private fun canAttack(target: Player): Boolean {
        return target.isValidTarget()
    }

    private fun StandardNpcAccess.setAttackVars(target: Player) {
        npc.lastAttack = mapClock
        npc.attackingPlayer = target.uid
        target.lastCombat = mapClock
        target.aggressiveNpc = npc.uid
    }
}
