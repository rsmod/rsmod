package org.rsmod.api.combat.formulas

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.accuracy.magic.NvPMagicAccuracy
import org.rsmod.api.combat.formulas.accuracy.magic.PvNMagicAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.NvNMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.NvPMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.PvNMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.PvPMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.ranged.NvPRangedAccuracy
import org.rsmod.api.combat.formulas.accuracy.ranged.PvNRangedAccuracy
import org.rsmod.api.combat.formulas.accuracy.ranged.PvPRangedAccuracy
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjType

public class AccuracyFormulae
@Inject
constructor(
    private val nvpMagicAccuracy: NvPMagicAccuracy,
    private val pvnMagicAccuracy: PvNMagicAccuracy,
    private val nvpMeleeAccuracy: NvPMeleeAccuracy,
    private val nvnMeleeAccuracy: NvNMeleeAccuracy,
    private val pvnMeleeAccuracy: PvNMeleeAccuracy,
    private val pvpMeleeAccuracy: PvPMeleeAccuracy,
    private val nvpRangedAccuracy: NvPRangedAccuracy,
    private val pvnRangedAccuracy: PvNRangedAccuracy,
    private val pvpRangedAccuracy: PvPRangedAccuracy,
) {
    /**
     * Rolls for melee accuracy to determine if an attack from a [player] against a [target] npc is
     * successful.
     *
     * This function calculates the hit chance based on the player's attack roll and the npc's
     * defence roll, then uses a value from the random number generator ([random]) to determine if
     * the attack hits.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, usually derived from the
     *   player's current stance.
     * @param attackStyle The [MeleeAttackStyle] used for the attack roll, usually derived from the
     *   player's current stance.
     * @param blockType The [MeleeAttackType] used for the defense roll. In most cases, this matches
     *   [attackType], but certain special attacks may use a different type.
     * @param specMultiplier A multiplier applied to the hit chance, typically used for special
     *   attacks.
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollMeleeAccuracy(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
        random: GameRandom,
    ): Boolean {
        val hitChance =
            getMeleeHitChance(player, target, attackType, attackStyle, blockType, specMultiplier)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the melee hit chance based on the [player]'s attack roll and the [target]'s
     * defence roll.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, usually derived from the
     *   [player]'s current stance.
     * @param attackStyle The [MeleeAttackStyle] used for the attack roll, usually derived from the
     *   [player]'s current stance.
     * @param blockType The [MeleeAttackType] used for the defense roll. In most cases, this matches
     *   [attackType], but certain special attacks may use a different type. For example, the Dragon
     *   Longsword special attack applies the player's current attack type for the attack roll but
     *   always uses `Slash` for the defense roll.
     * @param specMultiplier A multiplier applied to the hit chance, typically used for special
     *   attacks.
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getMeleeHitChance(
        player: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
    ): Int =
        pvnMeleeAccuracy.getHitChance(
            player = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specialMultiplier = specMultiplier,
        )

    /**
     * Rolls for melee accuracy to determine if an attack from a [player] against a [target] player
     * is successful.
     *
     * This function calculates the hit chance based on the player's attack roll and the target's
     * defence roll, then uses a value from the random number generator ([random]) to determine if
     * the attack hits.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, usually derived from the
     *   player's current stance.
     * @param attackStyle The [MeleeAttackStyle] used for the attack roll, usually derived from the
     *   player's current stance.
     * @param blockType The [MeleeAttackType] used for the defense roll. In most cases, this matches
     *   [attackType], but certain special attacks may use a different type.
     * @param specMultiplier A multiplier applied to the hit chance, typically used for special
     *   attacks.
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollMeleeAccuracy(
        player: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
        random: GameRandom,
    ): Boolean {
        val hitChance =
            getMeleeHitChance(player, target, attackType, attackStyle, blockType, specMultiplier)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the melee hit chance based on the [player]'s attack roll and the [target]'s
     * defence roll.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, usually derived from the
     *   [player]'s current stance.
     * @param attackStyle The [MeleeAttackStyle] used for the attack roll, usually derived from the
     *   [player]'s current stance.
     * @param blockType The [MeleeAttackType] used for the defense roll. In most cases, this matches
     *   [attackType], but certain special attacks may use a different type. For example, the Dragon
     *   Longsword special attack applies the player's current attack type for the attack roll but
     *   always uses `Slash` for the defense roll.
     * @param specMultiplier A multiplier applied to the hit chance, typically used for special
     *   attacks.
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getMeleeHitChance(
        player: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
    ): Int =
        pvpMeleeAccuracy.getHitChance(
            player = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specialMultiplier = specMultiplier,
        )

    /**
     * Rolls for melee accuracy to determine if an attack from an [npc] against a [target] player is
     * successful.
     *
     * This function calculates the hit chance based on the npc's attack roll and the player's
     * defence roll, then uses a value from the random number generator ([random]) to determine if
     * the attack hits.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, typically based on the
     *   npc's current type of attack.
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollMeleeAccuracy(
        npc: Npc,
        target: Player,
        attackType: MeleeAttackType?,
        random: GameRandom,
    ): Boolean {
        val hitChance = getMeleeHitChance(npc, target, attackType)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the melee hit chance based on the [npc]'s attack roll and the [target]'s defence
     * roll.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, typically based on the
     *   [npc]'s current type of attack.
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getMeleeHitChance(npc: Npc, target: Player, attackType: MeleeAttackType?): Int =
        nvpMeleeAccuracy.getHitChance(npc, target, attackType)

    /**
     * Rolls for melee accuracy to determine if an attack from an [npc] against a [target] npc is
     * successful.
     *
     * This function calculates the hit chance based on the npc's attack roll and the target npc's
     * defence roll, then uses a value from the random number generator ([random]) to determine if
     * the attack hits.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, typically based on the
     *   npc's current type of attack.
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollMeleeAccuracy(
        npc: Npc,
        target: Npc,
        attackType: MeleeAttackType?,
        random: GameRandom,
    ): Boolean {
        val hitChance = getMeleeHitChance(npc, target, attackType)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the melee hit chance based on the [npc]'s attack roll and the [target]'s defence
     * roll.
     *
     * @param attackType The [MeleeAttackType] used for the attack roll, typically based on the
     *   [npc]'s current type of attack.
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getMeleeHitChance(npc: Npc, target: Npc, attackType: MeleeAttackType?): Int =
        nvnMeleeAccuracy.getHitChance(npc, target, attackType)

    /**
     * Rolls for ranged accuracy to determine if an attack from a [player] against a [target] npc is
     * successful.
     *
     * This function calculates the hit chance based on the player's attack roll and the npc's
     * defence roll, then uses a value from the random number generator ([random]) to determine if
     * the attack hits.
     *
     * @param attackType The [RangedAttackType] used for the attack roll, usually derived from the
     *   player's current stance.
     * @param attackStyle The [RangedAttackStyle] used for the attack roll, usually derived from the
     *   player's current stance.
     * @param blockType The [RangedAttackType] used for the defence roll. In most cases, this
     *   matches [attackType], but certain special attacks may use a different type.
     * @param specMultiplier A multiplier applied to the hit chance, typically used for special
     *   attacks.
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollRangedAccuracy(
        player: Player,
        target: Npc,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        blockType: RangedAttackType?,
        specMultiplier: Double,
        random: GameRandom,
    ): Boolean {
        val hitChance =
            getRangedHitChance(player, target, attackType, attackStyle, blockType, specMultiplier)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the ranged hit chance based on the [player]'s attack roll and the [target]'s
     * defence roll.
     *
     * @param attackType The [RangedAttackType] used for the attack roll, usually derived from the
     *   [player]'s current stance.
     * @param attackStyle The [RangedAttackStyle] used for the attack roll, usually derived from the
     *   [player]'s current stance.
     * @param blockType The [RangedAttackType] used for the defence roll. In most cases, this
     *   matches [attackType], but certain special attacks may use a different type.
     * @param specMultiplier A multiplier applied to the hit chance, typically used for special
     *   attacks.
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getRangedHitChance(
        player: Player,
        target: Npc,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        blockType: RangedAttackType?,
        specMultiplier: Double,
    ): Int =
        pvnRangedAccuracy.getHitChance(
            player = player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specialMultiplier = specMultiplier,
        )

    /**
     * Rolls for ranged accuracy to determine if an attack from a [player] against a [target] player
     * is successful.
     *
     * This function calculates the hit chance based on the player's attack roll and the player's
     * defence roll, then uses a value from the random number generator ([random]) to determine if
     * the attack hits.
     *
     * @param attackStyle The [RangedAttackStyle] used for the attack roll, usually derived from the
     *   player's current stance.
     * @param specMultiplier A multiplier applied to the hit chance, typically used for special
     *   attacks.
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollRangedAccuracy(
        player: Player,
        target: Player,
        attackStyle: RangedAttackStyle?,
        specMultiplier: Double,
        random: GameRandom,
    ): Boolean {
        val hitChance = getRangedHitChance(player, target, attackStyle, specMultiplier)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the ranged hit chance based on the [player]'s attack roll and the [target]'s
     * defence roll.
     *
     * @param attackStyle The [RangedAttackStyle] used for the attack roll, usually derived from the
     *   [player]'s current stance.
     * @param specMultiplier A multiplier applied to the hit chance, typically used for special
     *   attacks.
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getRangedHitChance(
        player: Player,
        target: Player,
        attackStyle: RangedAttackStyle?,
        specMultiplier: Double,
    ): Int =
        pvpRangedAccuracy.getHitChance(
            player = player,
            target = target,
            attackStyle = attackStyle,
            specialMultiplier = specMultiplier,
        )

    /**
     * Rolls for ranged accuracy to determine if an attack from an [npc] against a [target] player
     * is successful.
     *
     * This function calculates the hit chance based on the npc's attack roll and the player's
     * defence roll, then uses a value from the random number generator ([random]) to determine if
     * the attack hits.
     *
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollRangedAccuracy(npc: Npc, target: Player, random: GameRandom): Boolean {
        val hitChance = getRangedHitChance(npc, target)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the ranged hit chance based on the [npc]'s attack roll and the [target]'s defence
     * roll.
     *
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getRangedHitChance(npc: Npc, target: Player): Int =
        nvpRangedAccuracy.getHitChance(npc, target)

    /**
     * Rolls for magic spell accuracy to determine if a spell cast by a [player] against a [target]
     * npc is successful.
     *
     * This function calculates the hit chance based on the player's magic accuracy and the target
     * npc's magic defence, then uses a value from the random number generator ([random]) to
     * determine if the spell hits.
     *
     * @param spell The [ObjType] representing the spell being cast (e.g., `objs.spell_wind_strike`
     *   for the Wind strike spell).
     * @param spellbook The [Spellbook] the spell belongs to (e.g., Standard or Ancients), usually
     *   derived from the player's current spellbook.
     * @param usedSunfireRune Whether the player used a Sunfire rune for the spell.
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollSpellAccuracy(
        player: Player,
        target: Npc,
        spell: ObjType,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
        random: GameRandom,
    ): Boolean {
        val hitChance = getSpellHitChance(player, target, spell, spellbook, usedSunfireRune)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the magic spell hit chance based on the [player]'s magic attack roll and the
     * [target]'s magic defence roll.
     *
     * @param spell The [ObjType] representing the spell being cast (e.g., `objs.spell_wind_strike`
     *   for the Wind strike spell).
     * @param spellbook The [Spellbook] the spell belongs to (e.g., Standard or Ancients), usually
     *   derived from the player's current spellbook.
     * @param usedSunfireRune Whether the player used a Sunfire rune for the spell.
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getSpellHitChance(
        player: Player,
        target: Npc,
        spell: ObjType,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
    ): Int =
        pvnMagicAccuracy.getSpellHitChance(
            player = player,
            target = target,
            spell = spell,
            spellbook = spellbook,
            usedSunfireRune = usedSunfireRune,
        )

    /**
     * Rolls for powered magic staff accuracy to determine if the built-in spell used by a [player]
     * against a [target] npc is successful.
     *
     * This function calculates the hit chance based on the player's magic accuracy and the target
     * npc's magic defence, then uses a value from the random number generator ([random]) to
     * determine whether the built-in spell hits.
     *
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollStaffAccuracy(player: Player, target: Npc, random: GameRandom): Boolean {
        val hitChance = getStaffHitChance(player, target)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the powered magic staff hit chance based on the [player]'s magic attack roll and
     * the [target]'s magic defence roll.
     *
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getStaffHitChance(player: Player, target: Npc): Int =
        pvnMagicAccuracy.getStaffHitChance(player = player, target = target)

    /**
     * Rolls for magic accuracy to determine if an attack from an [npc] against a [target] player is
     * successful.
     *
     * This function calculates the hit chance based on the npc's attack roll and the player's
     * defence roll, then uses a value from the random number generator ([random]) to determine if
     * the attack hits.
     *
     * @param random A [GameRandom] instance used to generate a random number for the hit roll.
     * @return `true` if the attack is successful (i.e., the hit chance exceeds the random roll),
     *   `false` otherwise.
     */
    public fun rollMagicAccuracy(npc: Npc, target: Player, random: GameRandom): Boolean {
        val hitChance = getMagicHitChance(npc, target)
        return isSuccessfulHit(hitChance, random)
    }

    /**
     * Calculates the magic hit chance based on the [npc]'s attack roll and the [target]'s defence
     * roll.
     *
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun getMagicHitChance(npc: Npc, target: Player): Int =
        nvpMagicAccuracy.getHitChance(npc, target)

    public companion object {
        public fun isSuccessfulHit(hitChance: Int, random: GameRandom): Boolean {
            val randomRoll = random.of(maxExclusive = HIT_CHANCE_SCALE)
            return hitChance > randomRoll
        }
    }
}
