package org.rsmod.api.combat.formulas

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.maxhit.magic.NvPMagicMaxHit
import org.rsmod.api.combat.formulas.maxhit.magic.PvNMagicMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.NvPMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.PvNMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.ranged.NvPRangedMaxHit
import org.rsmod.api.combat.formulas.maxhit.ranged.PvNRangedMaxHit
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjType

public class MaxHitFormulae
@Inject
constructor(
    private val pvnMagicMaxHit: PvNMagicMaxHit,
    private val nvpMagicMaxHit: NvPMagicMaxHit,
    private val pvnMeleeMaxHit: PvNMeleeMaxHit,
    private val nvpMeleeMaxHit: NvPMeleeMaxHit,
    private val pvnRangedMaxHit: PvNRangedMaxHit,
    private val nvpRangedMaxHit: NvPRangedMaxHit,
) {
    /** @see [PvNMeleeMaxHit.getMaxHit] */
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

    /** @see [PvNRangedMaxHit.getMaxHit] */
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

    public fun getRangedMaxHit(npc: Npc, target: Player): Int =
        nvpRangedMaxHit.getMaxHit(npc, target)

    /** @see [PvNMagicMaxHit.getSpellMaxHit] */
    public fun getSpellMaxHitRange(
        player: Player,
        target: Npc,
        spell: ObjType,
        spellbook: Spellbook?,
        baseMaxHit: Int,
        attackRate: Int,
        usedSunfireRune: Boolean,
    ): IntRange =
        pvnMagicMaxHit.getSpellMaxHit(
            player = player,
            target = target,
            spellbook = spellbook,
            spell = spell,
            baseMaxHit = baseMaxHit,
            attackRate = attackRate,
            usedSunfireRune = usedSunfireRune,
        )

    /** @see [PvNMagicMaxHit.getStaffMaxHit] */
    public fun getStaffMaxHitRange(player: Player, target: Npc, baseMaxHit: Int): Int =
        pvnMagicMaxHit.getStaffMaxHit(player, target, baseMaxHit = baseMaxHit)

    public fun getMagicMaxHit(npc: Npc, target: Player): Int = nvpMagicMaxHit.getMaxHit(npc, target)
}
