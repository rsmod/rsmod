package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.npc.attackRate
import org.rsmod.api.combat.commons.player.combatPlayDefendFx
import org.rsmod.api.combat.commons.player.queueCombatRetaliate
import org.rsmod.api.combat.formulas.MaxHitFormulae
import org.rsmod.api.config.refs.params
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.interact.AiPlayerInteractions
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.isValidTarget
import org.rsmod.api.player.output.soundSynth
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitType
import org.rsmod.game.type.obj.ObjTypeList

internal class PlayerCombat
@Inject
constructor(
    private val aiInteractions: AiPlayerInteractions,
    private val maxHits: MaxHitFormulae,
    private val objTypes: ObjTypeList,
) {
    fun attack(access: StandardNpcAccess, target: Player, attack: CombatAttack) {
        when (attack) {
            is CombatAttack.Melee -> access.attackMelee(target, attack)
            is CombatAttack.Ranged -> access.attackRanged(target, attack)
            is CombatAttack.Spell -> access.attackMagicSpell(target, attack)
            is CombatAttack.Staff -> access.attackMagicStaff(target, attack)
        }
    }

    private fun StandardNpcAccess.attackMelee(target: Player, attack: CombatAttack.Melee) {
        if (!canAttack(target)) {
            resetMode()
            return
        }

        if (actionDelay > mapClock) {
            return
        }

        val attackRate = npc.attackRate()
        actionDelay = mapClock + attackRate

        val attackAnim = npc.visType.param(params.attack_anim)
        val attackSound = npc.visType.paramOrNull(params.attack_sound)

        anim(attackAnim)
        attackSound?.let(target::soundSynth)

        // TODO(combat): Accuracy roll
        val maxHit = maxHits.getMeleeMaxHit(npc, target, attack.type)
        val damage = random.of(0..maxHit)

        target.queueHit(npc, 1, HitType.Melee, damage)
        target.combatPlayDefendFx(damage, objTypes)
        target.queueCombatRetaliate(npc)
    }

    private fun StandardNpcAccess.attackRanged(target: Player, attack: CombatAttack.Ranged) {
        TODO()
    }

    private fun StandardNpcAccess.attackMagicSpell(target: Player, attack: CombatAttack.Spell) {
        TODO()
    }

    private fun StandardNpcAccess.attackMagicStaff(target: Player, attack: CombatAttack.Staff) {
        TODO()
    }

    private fun canAttack(target: Player): Boolean {
        return target.isValidTarget()
    }
}
