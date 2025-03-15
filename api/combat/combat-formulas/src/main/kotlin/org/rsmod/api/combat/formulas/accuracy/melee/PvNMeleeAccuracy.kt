package org.rsmod.api.combat.formulas.accuracy.melee

import jakarta.inject.Inject
import java.util.EnumSet
import org.rsmod.api.combat.accuracy.npc.NpcMeleeAccuracy
import org.rsmod.api.combat.accuracy.player.PlayerMeleeAccuracy
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatWornAttributes
import org.rsmod.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.MeleeWornAttributeCollector
import org.rsmod.api.combat.formulas.isSlayerTask
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

public class PvNMeleeAccuracy
@Inject
constructor(
    private val bonuses: WornBonuses,
    private val npcCollector: CombatNpcAttributeCollector,
    private val wornCollector: MeleeWornAttributeCollector,
) {
    // TODO(combat): Consider moving this as an internal top level function instead. Depends
    //  on how ranged and magic hit chances are calculated.
    public fun isSuccessfulHit(hitChance: Int, random: GameRandom): Boolean {
        val randomRoll = random.of(maxExclusive = MeleeAccuracyOperations.HIT_CHANCE_SCALE)
        return hitChance > randomRoll
    }

    public fun getHitChance(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specialMultiplier: Double,
    ): Int {
        val hitChance =
            computeHitChance(
                source = player,
                target = target.visType,
                targetDefence = target.defenceLvl,
                targetCurrHp = target.hitpoints,
                targetMaxHp = target.baseHitpointsLvl,
                attackType = attackType,
                attackStyle = attackStyle,
                blockType = blockType,
                specialMultiplier = specialMultiplier,
            )
        // player.hitChance = hitChance // TODO(combat): Track with varp
        return hitChance
    }

    public fun computeHitChance(
        source: Player,
        target: UnpackedNpcType,
        targetDefence: Int,
        targetCurrHp: Int,
        targetMaxHp: Int,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specialMultiplier: Double,
    ): Int {
        val wornAttributes = wornCollector.collect(source, attackType)

        val slayerTask = target.isSlayerTask(source)
        val npcAttributes = npcCollector.collect(target, targetCurrHp, targetMaxHp, slayerTask)

        val baseAttackRoll =
            computeAttackRoll(source, attackType, attackStyle, wornAttributes, npcAttributes)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()

        val amascutInvocationLvl = 0 // TODO(combat): Decide if we want this to be varp or varn.
        val defenceRoll =
            computeDefenceRoll(
                target = target,
                targetDefence = targetDefence,
                amascutInvocationLvl = amascutInvocationLvl,
                blockType = blockType,
                npcAttributes = npcAttributes,
            )

        val hitChance = MeleeAccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
        return MeleeAccuracyOperations.modifyHitChance(
            hitChance = hitChance,
            attackRoll = attackRoll,
            defenceRoll = defenceRoll,
            wornAttributes = wornAttributes,
            npcAttributes = npcAttributes,
        )
    }

    public fun computeAttackRoll(
        source: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveAttack = MeleeAccuracyOperations.calculateEffectiveAttack(source, attackStyle)
        val attackBonus = source.getAttackBonus(attackType)
        val attackRoll = PlayerMeleeAccuracy.calculateBaseAttackRoll(effectiveAttack, attackBonus)
        return MeleeAccuracyOperations.modifyAttackRoll(attackRoll, wornAttributes, npcAttributes)
    }

    private fun Player.getAttackBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> bonuses.offensiveStabBonus(this)
            MeleeAttackType.Slash -> bonuses.offensiveSlashBonus(this)
            MeleeAttackType.Crush -> bonuses.offensiveCrushBonus(this)
            null -> 0
        }

    public fun computeDefenceRoll(
        target: UnpackedNpcType,
        targetDefence: Int,
        amascutInvocationLvl: Int,
        blockType: MeleeAttackType?,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        val effectiveDefence = NpcMeleeAccuracy.calculateEffectiveDefence(targetDefence)
        val defenceBonus = target.getDefenceBonus(blockType)
        val defenceRoll = NpcMeleeAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
        return MeleeAccuracyOperations.modifyDefenceRoll(
            defenceRoll = defenceRoll,
            amascutInvocationLvl = amascutInvocationLvl,
            npcAttributes = npcAttributes,
        )
    }

    private fun UnpackedNpcType.getDefenceBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> param(params.defence_stab)
            MeleeAttackType.Slash -> param(params.defence_slash)
            MeleeAttackType.Crush -> param(params.defence_crush)
            null -> 0
        }
}
