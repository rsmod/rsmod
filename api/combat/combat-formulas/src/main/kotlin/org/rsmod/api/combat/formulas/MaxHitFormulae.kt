package org.rsmod.api.combat.formulas

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.maxhit.magic.NvPMagicMaxHit
import org.rsmod.api.combat.formulas.maxhit.magic.PvNMagicMaxHit
import org.rsmod.api.combat.formulas.maxhit.magic.PvPMagicMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.NvNMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.NvPMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.PvNMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.PvPMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.ranged.NvNRangedMaxHit
import org.rsmod.api.combat.formulas.maxhit.ranged.NvPRangedMaxHit
import org.rsmod.api.combat.formulas.maxhit.ranged.PvNRangedMaxHit
import org.rsmod.api.combat.formulas.maxhit.ranged.PvPRangedMaxHit
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjType

public class MaxHitFormulae
@Inject
constructor(
    private val pvnMagicMaxHit: PvNMagicMaxHit,
    private val pvpMagicMaxHit: PvPMagicMaxHit,
    private val nvpMagicMaxHit: NvPMagicMaxHit,
    private val pvnMeleeMaxHit: PvNMeleeMaxHit,
    private val pvpMeleeMaxHit: PvPMeleeMaxHit,
    private val nvnMeleeMaxHit: NvNMeleeMaxHit,
    private val nvpMeleeMaxHit: NvPMeleeMaxHit,
    private val pvnRangedMaxHit: PvNRangedMaxHit,
    private val pvpRangedMaxHit: PvPRangedMaxHit,
    private val nvpRangedMaxHit: NvPRangedMaxHit,
    private val nvnRangedMaxHit: NvNRangedMaxHit,
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

    /** @see [PvPMeleeMaxHit.getMaxHit] */
    public fun getMeleeMaxHit(
        player: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specMultiplier: Double,
    ): Int =
        pvpMeleeMaxHit.getMaxHit(
            player = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specMultiplier,
        )

    /** @see [NvPMeleeMaxHit.getMaxHit] */
    public fun getMeleeMaxHit(npc: Npc, target: Player, attackType: MeleeAttackType?): Int =
        nvpMeleeMaxHit.getMaxHit(npc, target, attackType)

    /** @see [NvNMeleeMaxHit.getMaxHit] */
    public fun getMeleeMaxHit(npc: Npc): Int = nvnMeleeMaxHit.getMaxHit(npc)

    /** @see [PvNRangedMaxHit.getMaxHit] */
    public fun getRangedMaxHit(
        player: Player,
        target: Npc,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specMultiplier: Double,
        boltSpecDamage: Int,
    ): Int =
        pvnRangedMaxHit.getMaxHit(
            player = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specMultiplier,
            boltSpecDamage = boltSpecDamage,
        )

    /** @see [PvPRangedMaxHit.getMaxHit] */
    public fun getRangedMaxHit(
        player: Player,
        target: Player,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specMultiplier: Double,
        boltSpecDamage: Int,
    ): Int =
        pvpRangedMaxHit.getMaxHit(
            player = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specialMultiplier = specMultiplier,
            boltSpecDamage = boltSpecDamage,
        )

    /** @see [NvPRangedMaxHit.getMaxHit] */
    public fun getRangedMaxHit(npc: Npc, target: Player): Int =
        nvpRangedMaxHit.getMaxHit(npc, target)

    /** @see [NvNRangedMaxHit.getMaxHit] */
    public fun getRangedMaxHit(npc: Npc): Int = nvnRangedMaxHit.getMaxHit(npc)

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

    /** @see [PvPMagicMaxHit.getSpellMaxHit] */
    public fun getSpellMaxHitRange(
        player: Player,
        target: Player,
        spell: ObjType,
        spellbook: Spellbook?,
        baseMaxHit: Int,
        usedSunfireRune: Boolean,
    ): IntRange =
        pvpMagicMaxHit.getSpellMaxHit(
            player = player,
            target = target,
            spellbook = spellbook,
            spell = spell,
            baseMaxHit = baseMaxHit,
            usedSunfireRune = usedSunfireRune,
        )

    /** @see [PvNMagicMaxHit.getStaffMaxHit] */
    public fun getStaffMaxHit(
        player: Player,
        target: Npc,
        baseMaxHit: Int,
        specialMultiplier: Double,
    ): Int =
        pvnMagicMaxHit.getStaffMaxHit(
            player = player,
            target = target,
            baseMaxHit = baseMaxHit,
            specialMultiplier = specialMultiplier,
        )

    /** @see [PvPMagicMaxHit.getStaffMaxHit] */
    public fun getStaffMaxHit(
        player: Player,
        target: Player,
        baseMaxHit: Int,
        specialMultiplier: Double,
    ): Int =
        pvpMagicMaxHit.getStaffMaxHit(
            player = player,
            target = target,
            baseMaxHit = baseMaxHit,
            specialMultiplier = specialMultiplier,
        )

    /** @see [NvPMagicMaxHit.getMaxHit] */
    public fun getMagicMaxHit(npc: Npc, target: Player): Int = nvpMagicMaxHit.getMaxHit(npc, target)
}
