package org.rsmod.api.combat.formulas.accuracy.ranged

import jakarta.inject.Inject
import org.rsmod.api.combat.accuracy.player.PlayerRangedAccuracy
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.game.entity.Player

public class PvPRangedAccuracy
@Inject
constructor(private val bonuses: WornBonuses, private val attackStyles: AttackStyles) {
    public fun getHitChance(
        player: Player,
        target: Player,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
    ): Int = computeHitChance(player, target, attackStyle, specialMultiplier)

    public fun computeHitChance(
        source: Player,
        target: Player,
        attackStyle: RangedAttackStyle?,
        specialMultiplier: Double,
    ): Int {
        val baseAttackRoll = computeAttackRoll(source, attackStyle)
        val attackRoll = (baseAttackRoll * specialMultiplier).toInt()
        val defenceRoll = computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    // Note: This currently does _not_ take modifiers into account.
    // For accuracy, all modifiers - except the Twisted bow and Chinchompa fuses - are gated
    // behind npc attributes. It is unclear whether Chinchompa modifiers apply in pvp. If they
    // do, we should address this - either by allowing `RangedAccuracyOperations.modifyAttackRoll`
    // to differentiate between npc and player targets, or by adding a new function for pvp that
    // does not rely on npc attributes.
    public fun computeAttackRoll(source: Player, attackStyle: RangedAttackStyle?): Int {
        val effectiveRanged = RangedAccuracyOperations.calculateEffectiveRanged(source, attackStyle)
        val rangedBonus = bonuses.offensiveRangedBonus(source)
        return PlayerRangedAccuracy.calculateBaseAttackRoll(effectiveRanged, rangedBonus)
    }

    public fun computeDefenceRoll(target: Player): Int {
        val targetAttackStyle = attackStyles.get(target)
        val effectiveDefence =
            RangedAccuracyOperations.calculateEffectiveDefence(target, targetAttackStyle)
        val defenceBonus = bonuses.defensiveRangedBonus(target)
        return PlayerRangedAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }
}
