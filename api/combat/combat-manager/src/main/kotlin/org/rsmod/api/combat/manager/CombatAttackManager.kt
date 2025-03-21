package org.rsmod.api.combat.manager

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.npc.combatPlayDefendFx
import org.rsmod.api.combat.commons.npc.queueCombatRetaliate
import org.rsmod.api.combat.commons.player.combatPlayDefendFx
import org.rsmod.api.combat.commons.player.queueCombatRetaliate
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.AccuracyFormulae
import org.rsmod.api.combat.formulas.MaxHitFormulae
import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.queueHit
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.PlayerInteractions
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.random.GameRandom
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.Hit
import org.rsmod.game.hit.HitType
import org.rsmod.game.type.obj.ObjTypeList

public class CombatAttackManager
@Inject
constructor(
    private val random: GameRandom,
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val accuracy: AccuracyFormulae,
    private val maxHits: MaxHitFormulae,
    private val npcHitModifier: NpcHitModifier,
    private val npcInteractions: NpcInteractions,
    private val playerInteractions: PlayerInteractions,
) {
    /**
     * Sets [Player.actionDelay] to the current map clock + [cycles].
     *
     * This determines the delay before the player's next attack (after the current one completes).
     * For example, setting [cycles] to `2` allows the player to perform their next attack in `2`
     * cycles.
     *
     * **Note:** In most cases, systems that use attack managers should automatically apply the
     * appropriate delay based on the `attackrate` of the weapon being used, eliminating the need
     * for manual adjustments. This function is intended for special cases where fine-tuning or
     * custom attack delays are required.
     *
     * @param cycles The number of cycles to wait before the next attack can be performed.
     */
    public fun setNextAttackDelay(source: ProtectedAccess, cycles: Int) {
        source.actionDelay = source.mapClock + cycles
    }

    /**
     * Maintains combat engagement with the given [target] by calling the appropriate interaction
     * function.
     *
     * If [target] is an [Npc], this calls `opnpc2`; if [target] is a [Player], it calls
     * `opplayer2`. These interaction functions are responsible for keeping combat state active
     * between attacks.
     *
     * This function should be invoked after each successful attack. Failure to do so will result in
     * [source] breaking off combat until it is manually re-established or auto-retaliation occurs.
     */
    public fun continueCombat(source: ProtectedAccess, target: PathingEntity) {
        when (target) {
            is Npc -> continueCombat(source, target)
            is Player -> continueCombat(source, target)
        }
    }

    /**
     * Maintains combat engagement with the given [Npc] by invoking `opnpc2` through [source].
     *
     * This ensures that the combat interaction between [source] and [target] remains active after a
     * successful attack. If not called, [source] may unintentionally break off combat engagement
     * with the npc.
     */
    public fun continueCombat(source: ProtectedAccess, target: Npc) {
        source.opNpc2(target, npcInteractions)
    }

    /**
     * Maintains combat engagement with the given [Player] by invoking `opplayer2` through [source].
     *
     * This ensures that the combat interaction between [source] and [target] remains active after a
     * successful attack. If not called, [source] may unintentionally break off combat engagement
     * with the player.
     */
    public fun continueCombat(source: ProtectedAccess, target: Player) {
        source.opPlayer2(target, playerInteractions)
    }

    /**
     * Cancels the combat interaction while keeping the associated [Player.actionDelay] set to the
     * preset delay determined by the combat weapon's attack rate.
     *
     * This ensures that the player's action delay remains consistent with their last attack.
     *
     * This should be used when an attack was performed successfully. If the attack could not be
     * performed, consider using [clearCombat] instead.
     *
     * @see [clearCombat]
     */
    public fun stopCombat(source: ProtectedAccess) {
        source.stopAction(eventBus)
    }

    /**
     * Similar to [stopCombat], but resets the associated [Player.actionDelay] to the current map
     * clock.
     *
     * This should be used when an attack could not be performed and the combat interaction needs to
     * be terminated. Since the player did not actually attack, their action delay should be updated
     * to reflect that.
     */
    public fun clearCombat(source: ProtectedAccess) {
        source.stopAction(eventBus)
        setNextAttackDelay(source, 0)
    }

    /**
     * Rolls for melee damage against [target], handling both accuracy and damage calculations.
     *
     * This function first determines if the attack hits by calling [rollMeleeAccuracy]. If the
     * accuracy roll is successful, it then calculates the damage by calling [rollMeleeMaxHit].
     *
     * Note: Even if the accuracy roll succeeds, [rollMeleeMaxHit] can still roll `0` damage.
     *
     * @param accuracyBoost Percentage boost to accuracy (`0` = `+0%` boost, `100` = `+100%` boost).
     * @param maxHitBoost Percentage boost to max hit (`0` = `+0%` boost, `100` = `+100%` boost).
     * @param attackType The [MeleeAttackType] used for the [source]'s accuracy and max hit rolls.
     *   Usually based on the current `CombatAttack.Melee` attack but can be overridden.
     * @param attackStyle The [MeleeAttackStyle] used for the [source]'s accuracy and max hit rolls.
     *   Usually based on the current `CombatAttack.Melee` attack but can be overridden.
     * @param blockType The [MeleeAttackType] used for the [target]'s defensive roll. Usually the
     *   same as [attackType], but certain scenarios override this with a fixed value. For example,
     *   a Dragon longsword special attack always uses `Slash` for [blockType], even though
     *   [attackType] remains dynamic.
     * @return `0` if the accuracy roll fails; otherwise, a random damage value from `0` up to the
     *   calculated max hit.
     */
    public fun rollMeleeDamage(
        source: ProtectedAccess,
        target: PathingEntity,
        attack: CombatAttack.Melee,
        accuracyBoost: Int,
        maxHitBoost: Int,
        attackType: MeleeAttackType? = attack.type,
        attackStyle: MeleeAttackStyle? = attack.style,
        blockType: MeleeAttackType? = attack.type,
    ): Int {
        val successfulAccuracyRoll =
            rollMeleeAccuracy(
                source = source,
                target = target,
                percentBoost = accuracyBoost,
                attackType = attackType,
                attackStyle = attackStyle,
                blockType = blockType,
            )
        if (!successfulAccuracyRoll) {
            return 0
        }
        return rollMeleeMaxHit(source, target, attackType, attackStyle, maxHitBoost)
    }

    /**
     * Determines whether a melee attack from [source] will successfully hit [target].
     *
     * This function performs an accuracy roll by comparing [source]'s attack roll with [target]'s
     * defence roll, applying any specified accuracy boosts.
     *
     * @param attackType The [MeleeAttackType] used for the [source]'s accuracy calculation. Usually
     *   based on the current `CombatAttack.Melee` attack but can be overridden.
     * @param attackStyle The [MeleeAttackStyle] used for the [source]'s accuracy calculation.
     *   Usually based on the current `CombatAttack.Melee` attack but can be overridden.
     * @param blockType The [MeleeAttackType] used for the [target]'s defensive roll. Usually the
     *   same as [attackType], but certain scenarios override this with a fixed value. For example,
     *   a Dragon longsword special attack always uses `Slash` for [blockType], even though
     *   [attackType] remains dynamic.
     * @param percentBoost Percentage boost to accuracy (`0` = `+0%` boost, `100` = `+100%` boost).
     * @return `true` if the accuracy roll succeeds (the hit will "land"), `false` otherwise.
     */
    public fun rollMeleeAccuracy(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        percentBoost: Int,
    ): Boolean {
        val multiplier = 1 + (percentBoost / 100.0)
        return when (target) {
            is Npc -> {
                rollMeleeAccuracy(source, target, attackType, attackStyle, blockType, multiplier)
            }
            is Player -> {
                rollMeleeAccuracy(source, target, attackType, attackStyle, blockType, multiplier)
            }
        }
    }

    private fun rollMeleeAccuracy(
        source: ProtectedAccess,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
    ): Boolean =
        accuracy.rollMeleeAccuracy(
            player = source.player,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specMultiplier = specMultiplier,
            random = random,
        )

    private fun rollMeleeAccuracy(
        source: ProtectedAccess,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
    ): Boolean = TODO() // TODO(combat): pvp accuracy

    /**
     * Rolls a random damage value between `0` and the maximum possible melee hit.
     *
     * This function first calculates the maximum melee hit with any applicable boosts by calling
     * [calculateMeleeMaxHit], and then rolls a random value within that range.
     *
     * @param attackType The [MeleeAttackType] used for the [source]'s max hit calculation. Usually
     *   based on the current `CombatAttack.Melee` attack but can be overridden.
     * @param attackStyle The [MeleeAttackStyle] used for the [source]'s max hit calculation.
     *   Usually based on the current `CombatAttack.Melee` attack but can be overridden.
     * @param percentBoost Percentage boost to max hit (`0` = `+0%` boost, `100` = `+100%` boost).
     * @return A random damage value between `0` and the calculated max hit.
     */
    public fun rollMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        percentBoost: Int,
    ): Int {
        val maxHit = calculateMeleeMaxHit(source, target, attackType, attackStyle, percentBoost)
        return random.of(0..maxHit)
    }

    /**
     * Calculates the maximum melee hit that [source] can deal to [target].
     *
     * The maximum hit is determined based on [source]'s stats, equipment, combat bonuses, and the
     * provided attack parameters, with any specified percentage boosts applied.
     *
     * @param attackType The [MeleeAttackType] used for the [source]'s max hit calculation. Usually
     *   based on the current `CombatAttack.Melee` attack but can be overridden.
     * @param attackStyle The [MeleeAttackStyle] used for the [source]'s max hit calculation.
     *   Usually based on the current `CombatAttack.Melee` attack but can be overridden.
     * @param percentBoost Percentage boost to max hit (`0` = `+0%` boost, `100` = `+100%` boost).
     * @return The calculated maximum possible hit.
     */
    public fun calculateMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        percentBoost: Int,
    ): Int {
        val multiplier = 1 + (percentBoost / 100.0)
        return when (target) {
            is Npc -> calculateMeleeMaxHit(source, target, attackType, attackStyle, multiplier)
            is Player -> calculateMeleeMaxHit(source, target, attackType, attackStyle, multiplier)
        }
    }

    private fun calculateMeleeMaxHit(
        source: ProtectedAccess,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specMultiplier: Double,
    ): Int = maxHits.getMeleeMaxHit(source.player, target, attackType, attackStyle, specMultiplier)

    private fun calculateMeleeMaxHit(
        source: ProtectedAccess,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specMultiplier: Double,
    ): Int = TODO() // TODO(combat)

    /**
     * Queues a melee hit on [target], applying damage after the specified [delay].
     *
     * In addition to scheduling the hit, this also triggers appropriate defensive animations and
     * retaliation behavior for the [target], depending on whether it is an [Npc] or a [Player].
     *
     * The returned [Hit] represents the pending hit that will be applied to [target]. For [Npc]
     * targets, the final hit **may be modified** during **processing**, meaning the returned value
     * is not always guaranteed to match the final damage dealt. In contrast, hits against [Player]
     * targets are much more accurate and will only differ in rare situations (e.g., if the player
     * dies before the hit is applied).
     *
     * @param damage The damage to apply to [target]. This value may still be modified during hit
     *   processing.
     * @param delay The number of cycles to wait before applying the hit. This is usually `1` for
     *   melee hits.
     */
    public fun queueMeleeHit(
        source: ProtectedAccess,
        target: PathingEntity,
        damage: Int,
        delay: Int,
    ): Hit =
        when (target) {
            is Npc -> queueMeleeHit(source, target, damage, delay)
            is Player -> queueMeleeHit(source, target, damage, delay)
        }

    private fun queueMeleeHit(source: ProtectedAccess, target: Npc, damage: Int, delay: Int): Hit {
        val hit = target.queueHit(source.player, delay, HitType.Melee, damage, npcHitModifier)
        target.combatPlayDefendFx(source.player)
        target.queueCombatRetaliate(source.player)
        return hit
    }

    private fun queueMeleeHit(
        source: ProtectedAccess,
        target: Player,
        damage: Int,
        delay: Int,
    ): Hit {
        val hit = target.queueHit(source.player, delay, HitType.Melee, damage)
        target.combatPlayDefendFx(source.player, damage, objTypes)
        target.queueCombatRetaliate(source.player)
        return hit
    }
}
