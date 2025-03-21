package org.rsmod.api.combat.formulas

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.maxhit.melee.NvPMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.PvNMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.ranged.PvNRangedMaxHit
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public class MaxHitFormulae
@Inject
constructor(
    private val pvnMeleeMaxHit: PvNMeleeMaxHit,
    private val nvpMeleeMaxHit: NvPMeleeMaxHit,
    private val pvnRangedMaxHit: PvNRangedMaxHit,
) {
    public fun getMeleeMaxHit(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specMultiplier: Double,
    ): Int =
        pvnMeleeMaxHit.getMaxHit(
            player = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specMultiplier,
        )

    public fun getMeleeMaxHit(npc: Npc, target: Player, attackType: MeleeAttackType?): Int =
        nvpMeleeMaxHit.getMaxHit(npc, target, attackType)

    public fun getRangedMaxHit(
        player: Player,
        target: Npc,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specMultiplier: Double,
        boltSpecDamage: Int = 0,
    ): Int =
        pvnRangedMaxHit.getMaxHit(
            player = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specMultiplier,
            boltSpecDamage = boltSpecDamage,
        )
}
