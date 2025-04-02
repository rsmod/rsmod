package org.rsmod.api.combat.manager

import jakarta.inject.Inject
import kotlin.math.min
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.fx.MeleeAnimationAndSound
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.commons.npc.combatPlayDefendAnim
import org.rsmod.api.combat.commons.npc.combatPlayDefendSpot
import org.rsmod.api.combat.commons.npc.queueCombatRetaliate
import org.rsmod.api.combat.commons.npc.resolveCombatXpMultiplier
import org.rsmod.api.combat.commons.player.combatPlayDefendAnim
import org.rsmod.api.combat.commons.player.combatPlayDefendSpot
import org.rsmod.api.combat.commons.player.queueCombatRetaliate
import org.rsmod.api.combat.commons.player.resolveCombatXpMultiplier
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.AccuracyFormulae
import org.rsmod.api.combat.formulas.MaxHitFormulae
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.queueHit
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.NpcTInteractions
import org.rsmod.api.player.interact.PlayerInteractions
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.Hit
import org.rsmod.game.hit.HitType
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.proj.ProjAnim
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.proj.ProjAnimType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.map.CoordGrid

public class PlayerAttackManager
@Inject
constructor(
    private val random: GameRandom,
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val worldRepo: WorldRepository,
    private val accuracy: AccuracyFormulae,
    private val maxHits: MaxHitFormulae,
    private val npcHitModifier: NpcHitModifier,
    private val npcInteractions: NpcInteractions,
    private val npcTInteractions: NpcTInteractions,
    private val playerInteractions: PlayerInteractions,
) {
    /**
     * Determines if the player is still under an active attack delay.
     *
     * This returns `true` if the player's last attack delay has not yet expired, meaning they must
     * wait before performing their next attack.
     *
     * @return `true` if the player attack is still delayed; `false` if they can attack immediately.
     */
    public fun isAttackDelayed(player: Player): Boolean {
        return player.actionDelay > player.currentMapClock
    }

    /**
     * Sets player's attack delay to the current map clock + [cycles].
     *
     * This determines the delay before the player's **next** attack (after the current one). For
     * example, setting [cycles] to `2` allows the player to perform their next attack in `2`
     * cycles.
     *
     * @param cycles The number of cycles to wait before the next attack can be performed.
     */
    public fun setNextAttackDelay(player: Player, cycles: Int) {
        player.actionDelay = player.currentMapClock + cycles
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
    public fun continueCombat(source: Player, target: PathingEntity) {
        when (target) {
            is Npc -> continueCombat(source, target)
            is Player -> continueCombat(source, target)
        }
    }

    /**
     * Maintains combat engagement with the given [Npc] by invoking `opnpc2`.
     *
     * This ensures that the combat interaction between [source] and [target] remains active. If not
     * called, [source] may unintentionally break off combat engagement with the npc.
     */
    public fun continueCombat(source: Player, target: Npc) {
        npcInteractions.interact(source, target, InteractionOp.Op2)
    }

    /**
     * Maintains combat engagement with the given [Player] by invoking `opplayer2`.
     *
     * This ensures that the combat interaction between [source] and [target] remains active. If not
     * called, [source] may unintentionally break off combat engagement with the player.
     */
    public fun continueCombat(source: Player, target: Player) {
        playerInteractions.interact(source, target, InteractionOp.Op2)
    }

    /**
     * Maintains combat engagement with the given [Npc] by invoking `opnpct` using the
     * [MagicSpell.component] as the interaction component.
     *
     * This ensures that the combat interaction between [source] and [target] remains active. If not
     * called, [source] may unintentionally break off combat engagement with the npc.
     */
    public fun continueCombat(source: Player, target: Npc, spell: MagicSpell) {
        npcTInteractions.interact(source, target, spell.component, comsub = -1, null)
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
    public fun stopCombat(player: Player) {
        player.clearPendingAction(eventBus)
    }

    /**
     * Similar to [stopCombat], but resets the associated [Player.actionDelay] to the current map
     * clock.
     *
     * This should be used when an attack could not be performed and the combat interaction needs to
     * be terminated. Since the player did not actually attack, their action delay should be updated
     * to reflect that.
     *
     * **Warning:** This function forcibly resets the player's action delay without safeguards. If
     * the player currently has an active delay set by another mechanic, calling this will
     * incorrectly clear it. Consider checking [isAttackDelayed] and ensuring it returns `false`
     * before using this function. This is usually safe to call within combat attack logic; however,
     * anywhere outside of that (including initial ap/op engagement scripts for combat) should take
     * this warning into account.
     */
    public fun clearCombat(player: Player) {
        player.clearPendingAction(eventBus)
        setNextAttackDelay(player, 0)
    }

    /**
     * Plays the animation and sound effects for the given [attack], using the
     * [CombatAttack.Melee.weapon] obj type params and the [CombatAttack.Melee.stance] to determine
     * which effects to play.
     *
     * If no specific params are found on the weapon, default animations and sounds for the stance
     * are used.
     */
    public fun playWeaponFx(player: Player, attack: CombatAttack.Melee) {
        val weapon = objTypes.getOrNull(attack.weapon)

        val fx = MeleeAnimationAndSound.from(attack.stance)
        val (animParam, soundParam, defaultAnim, defaultSound) = fx

        val attackAnim = weapon?.paramOrNull(animParam) ?: defaultAnim
        val attackSound = weapon?.paramOrNull(soundParam) ?: defaultSound

        player.anim(attackAnim)
        player.soundSynth(attackSound)
    }

    /**
     * Plays the animation and sound effects for the given [attack], using the
     * [CombatAttack.Ranged.weapon] obj type params to determine which effects to play.
     *
     * If the weapon does not define an attack animation param (`attack_anim_stance1`), this
     * function will return `false` to indicate that the weapon is considered "broken" or invalid in
     * this context, and no effects will be played.
     *
     * @return `true` if the ranged weapon has an anim associated with param `attack_anim_stance1`.
     */
    public fun playWeaponFx(player: Player, attack: CombatAttack.Ranged): Boolean {
        val weapon = objTypes[attack.weapon]
        val attackAnim = weapon.paramOrNull(params.attack_anim_stance1) ?: return false
        val attackSound = weapon.paramOrNull(params.attack_sound_stance1)
        player.anim(attackAnim)
        attackSound?.let(player::soundSynth)
        return true
    }

    /**
     * Calculates and grants combat experience based on the given [attack], [damage], and the
     * [target]'s predefined combat xp multiplier.
     *
     * @param damage The damage rolled to be inflicted on [target]. This value is implicitly capped
     *   to the [target]'s remaining hitpoints.
     */
    public fun giveCombatXp(
        player: Player,
        target: PathingEntity,
        attack: CombatAttack.Melee,
        damage: Int,
    ): Unit =
        when (target) {
            is Npc -> giveCombatXp(player, target, attack, damage)
            is Player -> giveCombatXp(player, target, attack, damage)
        }

    private fun giveCombatXp(player: Player, target: Npc, attack: CombatAttack.Melee, damage: Int) {
        val cappedDamage = min(damage, target.hitpoints)
        val multiplier = target.resolveCombatXpMultiplier()
        giveCombatXp(player, attack, cappedDamage, multiplier)
    }

    private fun giveCombatXp(
        player: Player,
        target: Player,
        attack: CombatAttack.Melee,
        damage: Int,
    ) {
        val cappedDamage = min(damage, target.hitpoints)
        val multiplier = target.resolveCombatXpMultiplier()
        giveCombatXp(player, attack, cappedDamage, multiplier)
    }

    private fun giveCombatXp(
        player: Player,
        attack: CombatAttack.Melee,
        damage: Int,
        multiplier: Double = 1.0,
    ) {
        when (attack.style) {
            MeleeAttackStyle.Controlled -> {
                statAdvance(player, stats.attack, damage * 1.33, multiplier)
                statAdvance(player, stats.strength, damage * 1.33, multiplier)
                statAdvance(player, stats.defence, damage * 1.33, multiplier)
            }
            MeleeAttackStyle.Accurate -> {
                statAdvance(player, stats.attack, damage * 4.0, multiplier)
            }
            MeleeAttackStyle.Aggressive -> {
                statAdvance(player, stats.strength, damage * 4.0, multiplier)
            }
            MeleeAttackStyle.Defensive -> {
                statAdvance(player, stats.defence, damage * 4.0, multiplier)
            }
            null -> {
                /* no-op */
            }
        }
        statAdvance(player, stats.hitpoints, damage * 1.33, multiplier)
    }

    /**
     * Calculates and grants combat experience based on the given [attack], [damage], and the
     * [target]'s predefined combat xp multiplier.
     *
     * @param damage The damage rolled to be inflicted on [target]. This value is implicitly capped
     *   to the [target]'s remaining hitpoints.
     */
    public fun giveCombatXp(
        player: Player,
        target: PathingEntity,
        attack: CombatAttack.Ranged,
        damage: Int,
    ): Unit =
        when (target) {
            is Npc -> giveCombatXp(player, target, attack, damage)
            is Player -> giveCombatXp(player, target, attack, damage)
        }

    private fun giveCombatXp(
        player: Player,
        target: Npc,
        attack: CombatAttack.Ranged,
        damage: Int,
    ) {
        val cappedDamage = min(damage, target.hitpoints)
        val multiplier = target.resolveCombatXpMultiplier()
        giveCombatXp(player, attack, cappedDamage, multiplier)
    }

    private fun giveCombatXp(
        player: Player,
        target: Player,
        attack: CombatAttack.Ranged,
        damage: Int,
    ) {
        val cappedDamage = min(damage, target.hitpoints)
        val multiplier = target.resolveCombatXpMultiplier()
        giveCombatXp(player, attack, cappedDamage, multiplier)
    }

    private fun giveCombatXp(
        player: Player,
        attack: CombatAttack.Ranged,
        damage: Int,
        multiplier: Double,
    ) {
        when (attack.style) {
            RangedAttackStyle.Accurate -> {
                statAdvance(player, stats.ranged, damage * 4.0, multiplier)
            }
            RangedAttackStyle.Rapid -> {
                statAdvance(player, stats.ranged, damage * 4.0, multiplier)
            }
            RangedAttackStyle.Longrange -> {
                statAdvance(player, stats.ranged, damage * 2.0, multiplier)
                statAdvance(player, stats.defence, damage * 2.0, multiplier)
            }
            null -> {
                /* no-op */
            }
        }
        statAdvance(player, stats.hitpoints, damage * 1.33, multiplier)
    }

    /**
     * Calculates and grants combat experience based on the given [attack], [damage], and the
     * [target]'s predefined combat xp multiplier.
     *
     * @param damage The damage rolled to be inflicted on [target]. This value is implicitly capped
     *   to the [target]'s remaining hitpoints.
     */
    public fun giveCombatXp(
        player: Player,
        target: PathingEntity,
        attack: CombatAttack.Spell,
        damage: Int,
    ): Unit =
        when (target) {
            is Npc -> giveCombatXp(player, target, attack, damage)
            is Player -> giveCombatXp(player, target, attack, damage)
        }

    private fun giveCombatXp(player: Player, target: Npc, attack: CombatAttack.Spell, damage: Int) {
        val cappedDamage = min(damage, target.hitpoints)
        val multiplier = target.resolveCombatXpMultiplier()
        giveCombatXp(player, attack, cappedDamage, multiplier)
    }

    private fun giveCombatXp(
        player: Player,
        target: Player,
        attack: CombatAttack.Spell,
        damage: Int,
    ) {
        val cappedDamage = min(damage, target.hitpoints)
        val multiplier = target.resolveCombatXpMultiplier()
        giveCombatXp(player, attack, cappedDamage, multiplier)
    }

    private fun giveCombatXp(
        player: Player,
        attack: CombatAttack.Spell,
        damage: Int,
        multiplier: Double,
    ) {
        if (attack.defensive) {
            statAdvance(player, stats.magic, damage * 1.33, multiplier)
            statAdvance(player, stats.defence, damage.toDouble(), multiplier)
        } else {
            statAdvance(player, stats.magic, damage * 2.0, multiplier)
        }
        statAdvance(player, stats.hitpoints, damage * 1.33, multiplier)
    }

    /**
     * Calculates and grants combat experience based on the given [damage] and the [target]'s
     * predefined combat xp multiplier.
     *
     * @param damage The damage rolled to be inflicted on [target]. This value is implicitly capped
     *   to the [target]'s remaining hitpoints.
     */
    public fun giveCombatXp(
        player: Player,
        target: PathingEntity,
        attack: CombatAttack.Staff,
        damage: Int,
    ): Unit =
        when (target) {
            is Npc -> giveCombatXp(player, target, attack, damage)
            is Player -> giveCombatXp(player, target, attack, damage)
        }

    @Suppress("unused")
    private fun giveCombatXp(player: Player, target: Npc, attack: CombatAttack.Staff, damage: Int) {
        val cappedDamage = min(damage, target.hitpoints)
        val multiplier = target.resolveCombatXpMultiplier()
        giveStaffCombatXp(player, cappedDamage, multiplier)
    }

    @Suppress("unused")
    private fun giveCombatXp(
        player: Player,
        target: Player,
        attack: CombatAttack.Staff,
        damage: Int,
    ) {
        val cappedDamage = min(damage, target.hitpoints)
        val multiplier = target.resolveCombatXpMultiplier()
        giveStaffCombatXp(player, cappedDamage, multiplier)
    }

    private fun giveStaffCombatXp(player: Player, damage: Int, multiplier: Double) {
        statAdvance(player, stats.magic, damage * 2.0, multiplier)
        statAdvance(player, stats.hitpoints, damage * 1.33, multiplier)
    }

    private fun statAdvance(player: Player, stat: StatType, baseXp: Double, multiplier: Double) {
        player.statAdvance(stat, baseXp * multiplier)
    }

    /**
     * Rolls for melee damage against [target], handling both accuracy and damage calculations.
     *
     * This function first determines if the attack hits by calling [rollMeleeAccuracy]. If the
     * accuracy roll is successful, it then calculates the damage by calling [rollMeleeMaxHit].
     *
     * Note: Even if the accuracy roll succeeds, [rollMeleeMaxHit] can still return `0`.
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
        source: Player,
        target: PathingEntity,
        attack: CombatAttack.Melee,
        accuracyBoost: Int = 0,
        maxHitBoost: Int = 0,
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
        source: Player,
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
        source: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
    ): Boolean =
        accuracy.rollMeleeAccuracy(
            player = source,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specMultiplier = specMultiplier,
            random = random,
        )

    private fun rollMeleeAccuracy(
        source: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        blockType: MeleeAttackType?,
        specMultiplier: Double,
    ): Boolean =
        accuracy.rollMeleeAccuracy(
            player = source,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specMultiplier = specMultiplier,
            random = random,
        )

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
        source: Player,
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
        source: Player,
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
        source: Player,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specMultiplier: Double,
    ): Int = maxHits.getMeleeMaxHit(source, target, attackType, attackStyle, specMultiplier)

    private fun calculateMeleeMaxHit(
        source: Player,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specMultiplier: Double,
    ): Int = maxHits.getMeleeMaxHit(source, target, attackType, attackStyle, specMultiplier)

    /**
     * Queues a melee hit on [target], applying damage after the specified [delay].
     *
     * In addition to scheduling the hit, this also triggers the appropriate defensive animations
     * and retaliation behavior for the [target].
     *
     * The returned [Hit] represents the pending hit that will be applied to [target]. For [Npc]
     * targets, the final hit **may be modified** during **processing**, so the returned value is
     * not always guaranteed to match the final damage dealt. In contrast, hits against [Player]
     * targets are much more accurate and will only differ in rare situations (e.g., if the target
     * dies before the hit is applied).
     *
     * **Notes:**
     * - This function awards hero points to the [source] player based on the hit damage.
     * - This function **does not** grant combat experience to [source]. To apply experience, call
     *   [giveCombatXp] separately.
     *
     * @param damage The damage to apply to [target]. This value may still be modified during hit
     *   processing.
     * @param delay The number of cycles to wait before applying the hit. By default, this is set to
     *   `1` for melee hits.
     */
    public fun queueMeleeHit(
        source: Player,
        target: PathingEntity,
        damage: Int,
        delay: Int = 1,
    ): Hit =
        when (target) {
            is Npc -> queueMeleeHit(source, target, damage, delay)
            is Player -> queueMeleeHit(source, target, damage, delay)
        }

    private fun queueMeleeHit(source: Player, target: Npc, damage: Int, delay: Int): Hit {
        // Note: Retaliation must be queued _before_ the hit. If queued after, every hit would
        // trigger the "speed-up" death mechanic, since the hit queues would no longer be the
        // last entries in the queue list at the time of processing.
        target.queueCombatRetaliate(source)

        val hit = target.queueHit(source, delay, HitType.Melee, damage, npcHitModifier)
        target.heroPoints(source, min(hit.damage, target.hitpoints))
        target.combatPlayDefendAnim()
        return hit
    }

    private fun queueMeleeHit(source: Player, target: Player, damage: Int, delay: Int): Hit {
        // Note: Retaliation must be queued _before_ the hit. If queued after, every hit would
        // trigger the "speed-up" death mechanic, since the hit queues would no longer be the
        // last entries in the queue list at the time of processing.
        target.queueCombatRetaliate(source)

        val hit = target.queueHit(source, delay, HitType.Melee, damage)
        target.heroPoints(source, min(hit.damage, target.hitpoints))
        target.combatPlayDefendAnim(objTypes)
        return hit
    }

    /**
     * Rolls for ranged damage against [target], handling both accuracy and damage calculations.
     *
     * This function first determines if the attack hits by calling [rollRangedAccuracy]. If the
     * accuracy roll is successful, it then calculates the damage by calling [rollRangedMaxHit].
     *
     * Note: Even if the accuracy roll succeeds, [rollRangedMaxHit] can still return `0`.
     *
     * @param accuracyBoost Percentage boost to accuracy (`0` = `+0%` boost, `100` = `+100%` boost).
     * @param maxHitBoost Percentage boost to max hit (`0` = `+0%` boost, `100` = `+100%` boost).
     * @param attackType The [RangedAttackType] used for the [source]'s accuracy and max hit rolls.
     *   Usually based on the current `CombatAttack.Ranged` attack but can be overridden.
     * @param attackStyle The [RangedAttackStyle] used for the [source]'s accuracy and max hit
     *   rolls. Usually based on the current `CombatAttack.Ranged` attack but can be overridden.
     * @param blockType The [RangedAttackType] used for the [target]'s defensive roll. Usually the
     *   same as [attackType], but certain scenarios override this with a fixed value.
     * @param boltSpecDamage The additive bonus damage from bolt special attacks. For example, Opal
     *   bolts (e) special should set this value to `visible ranged level * 10%, rounded down`.
     * @return `0` if the accuracy roll fails; otherwise, a random damage value from `0` up to the
     *   calculated max hit.
     */
    public fun rollRangedDamage(
        source: Player,
        target: PathingEntity,
        attack: CombatAttack.Ranged,
        accuracyBoost: Int = 0,
        maxHitBoost: Int = 0,
        attackType: RangedAttackType? = attack.type,
        attackStyle: RangedAttackStyle? = attack.style,
        blockType: RangedAttackType? = attack.type,
        boltSpecDamage: Int = 0,
    ): Int {
        val successfulAccuracyRoll =
            rollRangedAccuracy(
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
        return rollRangedMaxHit(
            source = source,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            percentBoost = maxHitBoost,
            boltSpecDamage = boltSpecDamage,
        )
    }

    /**
     * Determines whether a ranged attack from [source] will successfully hit [target].
     *
     * This function performs an accuracy roll by comparing [source]'s attack roll with [target]'s
     * defence roll, applying any specified accuracy boosts.
     *
     * @param attackType The [RangedAttackType] used for the [source]'s accuracy calculation.
     *   Usually based on the current `CombatAttack.Ranged` attack but can be overridden.
     * @param attackStyle The [RangedAttackStyle] used for the [source]'s accuracy calculation.
     *   Usually based on the current `CombatAttack.Ranged` attack but can be overridden.
     * @param blockType The [RangedAttackType] used for the [target]'s defensive roll. Usually the
     *   same as [attackType], but certain scenarios override this with a fixed value.
     * @param percentBoost Percentage boost to accuracy (`0` = `+0%` boost, `100` = `+100%` boost).
     * @return `true` if the accuracy roll succeeds (the hit will "land"), `false` otherwise.
     */
    public fun rollRangedAccuracy(
        source: Player,
        target: PathingEntity,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        blockType: RangedAttackType?,
        percentBoost: Int,
    ): Boolean {
        val multiplier = 1 + (percentBoost / 100.0)
        return when (target) {
            is Npc -> {
                rollRangedAccuracy(source, target, attackType, attackStyle, blockType, multiplier)
            }
            is Player -> rollRangedAccuracy(source, target, attackType, attackStyle, multiplier)
        }
    }

    private fun rollRangedAccuracy(
        source: Player,
        target: Npc,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        blockType: RangedAttackType?,
        specMultiplier: Double,
    ): Boolean =
        accuracy.rollRangedAccuracy(
            player = source,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            blockType = blockType,
            specMultiplier = specMultiplier,
            random = random,
        )

    private fun rollRangedAccuracy(
        source: Player,
        target: Player,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specMultiplier: Double,
    ): Boolean =
        accuracy.rollRangedAccuracy(
            player = source,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specMultiplier = specMultiplier,
            random = random,
        )

    /**
     * Rolls a random damage value between `0` and the maximum possible ranged hit.
     *
     * This function first calculates the maximum ranged hit with any applicable boosts by calling
     * [calculateRangedMaxHit], and then rolls a random value within that range.
     *
     * @param attackType The [RangedAttackType] used for the [source]'s max hit calculation. Usually
     *   based on the current `CombatAttack.Ranged` attack but can be overridden.
     * @param attackStyle The [RangedAttackStyle] used for the [source]'s max hit calculation.
     *   Usually based on the current `CombatAttack.Ranged` attack but can be overridden.
     * @param percentBoost Percentage boost to max hit (`0` = `+0%` boost, `100` = `+100%` boost).
     * @param boltSpecDamage The additive bonus damage from bolt special attacks. For example, Opal
     *   bolts (e) special should set this value to `visible ranged level * 10%, rounded down`.
     * @return A random damage value between `0` and the calculated max hit.
     */
    public fun rollRangedMaxHit(
        source: Player,
        target: PathingEntity,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        percentBoost: Int,
        boltSpecDamage: Int,
    ): Int {
        val maxHit =
            calculateRangedMaxHit(
                source = source,
                target = target,
                attackType = attackType,
                attackStyle = attackStyle,
                percentBoost = percentBoost,
                boltSpecDamage = boltSpecDamage,
            )
        return random.of(0..maxHit)
    }

    /**
     * Calculates the maximum ranged hit that [source] can deal to [target].
     *
     * The maximum hit is determined based on [source]'s stats, equipment, combat bonuses, and the
     * provided attack parameters, with any specified percentage boosts and bolt special damage
     * applied.
     *
     * @param attackType The [RangedAttackType] used for the [source]'s max hit calculation. Usually
     *   based on the current `CombatAttack.Ranged` attack but can be overridden.
     * @param attackStyle The [RangedAttackStyle] used for the [source]'s max hit calculation.
     *   Usually based on the current `CombatAttack.Ranged` attack but can be overridden.
     * @param percentBoost Percentage boost to max hit (`0` = `+0%` boost, `100` = `+100%` boost).
     * @param boltSpecDamage The additive bonus damage from bolt special attacks. For example, Opal
     *   bolts (e) special should set this value to `visible ranged level * 10%, rounded down`.
     * @return The calculated maximum possible hit.
     */
    public fun calculateRangedMaxHit(
        source: Player,
        target: PathingEntity,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        percentBoost: Int,
        boltSpecDamage: Int,
    ): Int {
        val multiplier = 1 + (percentBoost / 100.0)
        return when (target) {
            is Npc ->
                calculateRangedMaxHit(
                    source = source,
                    target = target,
                    attackType = attackType,
                    attackStyle = attackStyle,
                    specMultiplier = multiplier,
                    boltSpecDamage = boltSpecDamage,
                )
            is Player ->
                calculateRangedMaxHit(
                    source = source,
                    target = target,
                    attackType = attackType,
                    attackStyle = attackStyle,
                    specMultiplier = multiplier,
                    boltSpecDamage = boltSpecDamage,
                )
        }
    }

    private fun calculateRangedMaxHit(
        source: Player,
        target: Npc,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specMultiplier: Double,
        boltSpecDamage: Int,
    ): Int =
        maxHits.getRangedMaxHit(
            player = source,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specMultiplier = specMultiplier,
            boltSpecDamage = boltSpecDamage,
        )

    private fun calculateRangedMaxHit(
        source: Player,
        target: Player,
        attackType: RangedAttackType?,
        attackStyle: RangedAttackStyle?,
        specMultiplier: Double,
        boltSpecDamage: Int,
    ): Int =
        maxHits.getRangedMaxHit(
            player = source,
            target = target,
            attackType = attackType,
            attackStyle = attackStyle,
            specMultiplier = specMultiplier,
            boltSpecDamage = boltSpecDamage,
        )

    /**
     * Queues a ranged hit on [target], applying damage after the specified [hitDelay].
     *
     * In addition to scheduling the hit, this also triggers the appropriate defensive animations
     * and retaliation behavior for the [target].
     *
     * The returned [Hit] represents the pending hit that will be applied to [target]. For [Npc]
     * targets, the final hit **may be modified** during **processing**, so the returned value is
     * not always guaranteed to match the final damage dealt. In contrast, hits against [Player]
     * targets are much more accurate and will only differ in rare situations (e.g., if the target
     * dies before the hit is applied).
     *
     * **Notes:**
     * - This function awards hero points to the [source] player based on the hit damage.
     * - This function **does not** grant combat experience to [source]. To apply experience, call
     *   [giveCombatXp] separately.
     *
     * @param ammo Sets the [Hit.secondaryObj] to the provided value. Some hit scripts may rely on
     *   this for special logic. For ranged attacks, this should be the ammunition used by [source]
     *   for the attack. For example, if the player is using a Magic shortbow with Rune arrows, this
     *   should be set to the Rune arrows obj type. If the player is using a thrown weapon, pass
     *   `null` for this parameter, as the thrown weapon is already stored in [Hit.righthandObj].
     * @param damage The damage to apply to [target]. This value may still be modified during hit
     *   processing.
     * @param clientDelay The delay in client cycles (`20ms` per cycle) before the projectile
     *   visually lands on the target. This is usually derived from the projectile's metadata and
     *   determines when the [target]'s block animation should play.
     * @param hitDelay The number of server cycles to wait before applying the hit, usually
     *   calculated as `1 + (clientDelay / 30)`.
     */
    public fun queueRangedHit(
        source: Player,
        target: PathingEntity,
        ammo: ObjType?,
        damage: Int,
        clientDelay: Int,
        hitDelay: Int,
    ): Hit =
        when (target) {
            is Npc -> queueRangedHit(source, target, ammo, damage, clientDelay, hitDelay)
            is Player -> queueRangedHit(source, target, ammo, damage, clientDelay, hitDelay)
        }

    private fun queueRangedHit(
        source: Player,
        target: Npc,
        ammo: ObjType?,
        damage: Int,
        clientDelay: Int,
        hitDelay: Int,
    ): Hit {
        // Note: Retaliation must be queued _before_ the hit. If queued after, every hit would
        // trigger the "speed-up" death mechanic, since the hit queues would no longer be the
        // last entries in the queue list at the time of processing.
        target.queueCombatRetaliate(source, hitDelay)

        val hit =
            target.queueHit(
                source = source,
                delay = hitDelay,
                type = HitType.Ranged,
                damage = damage,
                modifier = npcHitModifier,
                sourceSecondary = ammo,
            )
        target.heroPoints(source, min(hit.damage, target.hitpoints))
        target.combatPlayDefendAnim(clientDelay)
        target.combatPlayDefendSpot(objTypes, ammo, clientDelay)
        return hit
    }

    private fun queueRangedHit(
        source: Player,
        target: Player,
        ammo: ObjType?,
        damage: Int,
        clientDelay: Int,
        hitDelay: Int,
    ): Hit {
        // Note: Retaliation must be queued _before_ the hit. If queued after, every hit would
        // trigger the "speed-up" death mechanic, since the hit queues would no longer be the
        // last entries in the queue list at the time of processing.
        target.queueCombatRetaliate(source, hitDelay)

        val hit =
            target.queueHit(
                source = source,
                delay = hitDelay,
                type = HitType.Ranged,
                damage = damage,
                sourceSecondary = ammo,
            )
        target.heroPoints(source, min(hit.damage, target.hitpoints))
        target.combatPlayDefendAnim(objTypes, clientDelay)
        target.combatPlayDefendSpot(objTypes, ammo, clientDelay)
        return hit
    }

    /**
     * Queues a ranged hit on [target], applying damage after the specified [hitDelay].
     *
     * Unlike [queueRangedHit], this function **does not** trigger any block animations, visual
     * effects, or retaliation behavior from the [target]. It simply applies the damage after the
     * delay, while also awarding hero points to [source] based on the damage.
     *
     * This is useful for situations where damage should occur silently or as part of a scripted
     * sequence where no defensive behavior from the target is required (e.g., secondary hits from
     * Dark bow).
     *
     * @see [queueRangedHit]
     */
    public fun queueRangedDamage(
        source: Player,
        target: PathingEntity,
        ammo: ObjType?,
        damage: Int,
        hitDelay: Int,
    ): Hit =
        when (target) {
            is Npc -> queueRangedDamage(source, target, ammo, damage, hitDelay)
            is Player -> queueRangedDamage(source, target, ammo, damage, hitDelay)
        }

    private fun queueRangedDamage(
        source: Player,
        target: Player,
        ammo: ObjType?,
        damage: Int,
        hitDelay: Int,
    ): Hit {
        val hit =
            target.queueHit(
                source = source,
                delay = hitDelay,
                type = HitType.Ranged,
                damage = damage,
                sourceSecondary = ammo,
            )
        target.heroPoints(source, min(hit.damage, target.hitpoints))
        return hit
    }

    private fun queueRangedDamage(
        source: Player,
        target: Npc,
        ammo: ObjType?,
        damage: Int,
        hitDelay: Int,
    ): Hit {
        val hit =
            target.queueHit(
                source = source,
                delay = hitDelay,
                type = HitType.Ranged,
                damage = damage,
                modifier = npcHitModifier,
                sourceSecondary = ammo,
            )
        target.heroPoints(source, min(hit.damage, target.hitpoints))
        return hit
    }

    /**
     * Determines whether a magic spell cast by [source] will successfully hit [target].
     *
     * This function performs an accuracy roll by comparing [source]'s magic attack roll with
     * [target]'s magic defence roll.
     *
     * @param spell The [ObjType] representing the spell being cast (e.g., `objs.spell_wind_strike`
     *   for the Wind Strike spell).
     * @param spellbook The [Spellbook] the spell belongs to (e.g., Standard or Ancients), usually
     *   derived from the player's current spellbook.
     * @param sunfireRune Set to `true` if the spell was cast using a Sunfire rune.
     * @return `true` if the accuracy roll succeeds (the spell will "land"), `false` otherwise.
     */
    public fun rollSpellAccuracy(
        source: Player,
        target: PathingEntity,
        spell: ObjType,
        spellbook: Spellbook?,
        sunfireRune: Boolean,
    ): Boolean =
        when (target) {
            is Npc -> rollSpellAccuracy(source, target, spell, spellbook, sunfireRune)
            is Player -> rollSpellAccuracy(source, target, spell, spellbook, sunfireRune)
        }

    private fun rollSpellAccuracy(
        source: Player,
        target: Npc,
        spell: ObjType,
        spellbook: Spellbook?,
        sunfireRune: Boolean,
    ): Boolean =
        accuracy.rollSpellAccuracy(
            player = source,
            target = target,
            spell = spell,
            spellbook = spellbook,
            usedSunfireRune = sunfireRune,
            random = random,
        )

    private fun rollSpellAccuracy(
        source: Player,
        target: Player,
        spell: ObjType,
        spellbook: Spellbook?,
        sunfireRune: Boolean,
    ): Boolean = TODO() // TODO(combat): pvp accuracy

    /**
     * Rolls a magic spell damage value between the minimum and maximum possible spell hit.
     *
     * This function first calculates the minimum and maximum hit by calling [calculateSpellMaxHit],
     * then rolls a random value within that range. The minimum hit is usually `0`; however, certain
     * modifiers - such as one enabled through [sunfireRune] - can affect this value.
     *
     * @param spell The [ObjType] representing the spell being cast (e.g., `objs.spell_wind_strike`
     *   for the Wind strike spell).
     * @param spellbook The [Spellbook] the spell belongs to (e.g., Standard or Ancients), usually
     *   derived from the player's current spellbook.
     * @param baseMaxHit The spell's base max hit, used as a baseline for calculating the maximum
     *   (and minimum, when applicable) hit.
     * @param attackRate The delay in server cycles at which the player is currently attacking. This
     *   is usually `5` for spell attacks.
     * @param sunfireRune Set to `true` if the spell was cast using a Sunfire rune. Sunfire runes
     *   apply modifiers, such as increasing the minimum hit for Fire-based spells.
     * @return A random damage value between the calculated minimum and maximum hit.
     */
    public fun rollSpellMaxHit(
        source: Player,
        target: PathingEntity,
        spell: ObjType,
        spellbook: Spellbook?,
        baseMaxHit: Int,
        attackRate: Int,
        sunfireRune: Boolean,
    ): Int {
        val hitRange =
            calculateSpellMaxHit(
                source = source,
                target = target,
                spell = spell,
                spellbook = spellbook,
                baseMaxHit = baseMaxHit,
                attackRate = attackRate,
                sunfireRune = sunfireRune,
            )
        return random.of(hitRange)
    }

    /**
     * Calculates the minimum and maximum magic spell hit that [source] can deal to [target].
     *
     * The hit range is determined based on the spell's base max hit, the player's attack speed,
     * spellbook, and other modifiers - such as the use of a Sunfire rune. The minimum hit is
     * usually `0`; however, modifiers like Sunfire runes can increase the lower bound for certain
     * spells.
     *
     * @param spell The [ObjType] representing the spell being cast (e.g., `objs.spell_wind_strike`
     *   for the Wind Strike spell).
     * @param spellbook The [Spellbook] the spell belongs to (e.g., Standard or Ancients), usually
     *   derived from the player's current spellbook.
     * @param baseMaxHit The spell's base max hit, used as a baseline for calculating the maximum
     *   (and minimum, when applicable) hit.
     * @param attackRate The delay in server cycles at which the player is currently attacking. This
     *   is usually `5` for spell attacks.
     * @param sunfireRune Set to `true` if the spell was cast using a Sunfire rune. Sunfire runes
     *   apply modifiers, such as increasing the minimum hit for Fire-based spells.
     * @return A range representing the minimum and maximum possible hit values for the spell.
     */
    public fun calculateSpellMaxHit(
        source: Player,
        target: PathingEntity,
        spell: ObjType,
        spellbook: Spellbook?,
        baseMaxHit: Int,
        attackRate: Int,
        sunfireRune: Boolean,
    ): IntRange =
        when (target) {
            is Npc ->
                calculateSpellMaxHit(
                    source = source,
                    target = target,
                    spell = spell,
                    spellbook = spellbook,
                    baseMaxHit = baseMaxHit,
                    attackRate = attackRate,
                    sunfireRune = sunfireRune,
                )
            is Player ->
                calculateSpellMaxHit(
                    source = source,
                    target = target,
                    spell = spell,
                    spellbook = spellbook,
                    baseMaxHit = baseMaxHit,
                    attackRate = attackRate,
                    sunfireRune = sunfireRune,
                )
        }

    private fun calculateSpellMaxHit(
        source: Player,
        target: Npc,
        spell: ObjType,
        spellbook: Spellbook?,
        baseMaxHit: Int,
        attackRate: Int,
        sunfireRune: Boolean,
    ): IntRange =
        maxHits.getSpellMaxHitRange(
            player = source,
            target = target,
            spell = spell,
            spellbook = spellbook,
            baseMaxHit = baseMaxHit,
            attackRate = attackRate,
            usedSunfireRune = sunfireRune,
        )

    private fun calculateSpellMaxHit(
        source: Player,
        target: Player,
        spell: ObjType,
        spellbook: Spellbook?,
        baseMaxHit: Int,
        attackRate: Int,
        sunfireRune: Boolean,
    ): IntRange = TODO() // TODO(combat)

    /**
     * Determines whether the **built-in spell** from a **powered staff** used by [source] will
     * successfully hit [target].
     *
     * This function performs an accuracy roll by comparing [source]'s magic attack roll with
     * [target]'s magic defence roll.
     *
     * @param attackStyle The [MagicAttackStyle] used for the [source]'s accuracy calculation.
     *   Usually based on the current `CombatAttack.Staff` attack but can be overridden.
     * @param percentBoost Percentage boost to accuracy (`0` = `+0%` boost, `100` = `+100%` boost).
     * @return `true` if the accuracy roll succeeds (the spell will "land"), `false` otherwise.
     */
    public fun rollStaffAccuracy(
        source: Player,
        target: PathingEntity,
        attackStyle: MagicAttackStyle?,
        percentBoost: Int,
    ): Boolean {
        val multiplier = 1 + (percentBoost / 100.0)
        return when (target) {
            is Npc -> rollStaffAccuracy(source, target, attackStyle, multiplier)
            is Player -> rollStaffAccuracy(source, target, attackStyle, multiplier)
        }
    }

    private fun rollStaffAccuracy(
        source: Player,
        target: Npc,
        attackStyle: MagicAttackStyle?,
        specMultiplier: Double,
    ): Boolean =
        accuracy.rollStaffAccuracy(
            player = source,
            target = target,
            attackStyle,
            specMultiplier,
            random,
        )

    private fun rollStaffAccuracy(
        source: Player,
        target: Player,
        attackStyle: MagicAttackStyle?,
        specMultiplier: Double,
    ): Boolean =
        accuracy.rollStaffAccuracy(
            player = source,
            target = target,
            attackStyle,
            specMultiplier,
            random,
        )

    /**
     * Rolls a random damage value between `0` and the maximum possible hit for a powered staff's
     * built-in spell.
     *
     * This function first calculates the maximum magic hit for the built-in spell by calling
     * [calculateStaffMaxHit], and then rolls a random value within that range.
     *
     * @param baseMaxHit The base max hit of the powered staff's built-in spell before any modifiers
     *   are applied.
     * @param percentBoost Percentage boost to max hit (`0` = `+0%` boost, `100` = `+100%` boost).
     * @return A random damage value between `0` and the calculated max hit.
     */
    public fun rollStaffMaxHit(
        source: Player,
        target: PathingEntity,
        baseMaxHit: Int,
        percentBoost: Int,
    ): Int {
        val maxHit = calculateStaffMaxHit(source, target, baseMaxHit, percentBoost = percentBoost)
        return random.of(0..maxHit)
    }

    /**
     * Calculates the maximum magic hit that a **powered staff**'s built-in spell can deal to
     * [target] from [source].
     *
     * The maximum hit is determined based on the [source]'s magic stats, the [target]'s relevant
     * magic and defence stats, and the provided [baseMaxHit] of the staff's built-in spell.
     *
     * @param baseMaxHit The base max hit of the powered staff's built-in spell before any modifiers
     *   are applied.
     * @param percentBoost Percentage boost to max hit (`0` = `+0%` boost, `100` = `+100%` boost).
     * @return The calculated maximum possible hit.
     */
    public fun calculateStaffMaxHit(
        source: Player,
        target: PathingEntity,
        baseMaxHit: Int,
        percentBoost: Int,
    ): Int {
        val multiplier = 1 + (percentBoost / 100.0)
        return when (target) {
            is Npc -> calculateSpellMaxHit(source, target, baseMaxHit, multiplier)
            is Player -> calculateSpellMaxHit(source, target, baseMaxHit, multiplier)
        }
    }

    private fun calculateSpellMaxHit(
        source: Player,
        target: Npc,
        baseMaxHit: Int,
        specMultiplier: Double,
    ): Int =
        maxHits.getStaffMaxHit(
            source,
            target,
            baseMaxHit = baseMaxHit,
            specialMultiplier = specMultiplier,
        )

    private fun calculateSpellMaxHit(
        source: Player,
        target: Player,
        baseMaxHit: Int,
        specMultiplier: Double,
    ): Int = TODO() // TODO(combat)

    /**
     * Queues a magic hit on [target], applying damage after the specified [hitDelay].
     *
     * In addition to scheduling the hit, this also triggers the appropriate defensive animations
     * and retaliation behavior for the [target].
     *
     * The returned [Hit] represents the pending hit that will be applied to [target]. For [Npc]
     * targets, the final hit **may be modified** during **processing**, so the returned value is
     * not always guaranteed to match the final damage dealt. In contrast, hits against [Player]
     * targets are much more accurate and will only differ in rare situations (e.g., if the target
     * dies before the hit is applied).
     *
     * **Notes:**
     * - This function awards hero points to the [source] player based on the hit damage.
     * - This function **does not** grant combat experience to [source]. To apply experience, call
     *   [giveCombatXp] separately.
     * - This function **does not** handle magic sounds or impact spotanims. These must be handled
     *   separately by calling [playMagicHitFx], or [playMagicSplashFx] for splashed hits.
     *
     * @param spell Sets the [Hit.secondaryObj] to the provided value. Some hit scripts may rely on
     *   this for special logic. For magic attacks, this should be the spell-associated obj used by
     *   [source] for the attack. For example, if the player attacks with the Wind strike spell,
     *   this should be set to `objs.spell_wind_strike`.
     * @param damage The damage to apply to [target]. This value may still be modified during hit
     *   processing.
     * @param clientDelay The delay in client cycles (`20ms` per cycle) before the projectile
     *   visually lands on the target. This is usually derived from the projectile's metadata and
     *   determines when the [target]'s block animation should play.
     * @param hitDelay The number of server cycles to wait before applying the hit, usually
     *   calculated as `1 + (clientDelay / 30)`.
     * @param retaliationDelay The number of server cycles to set as the retaliation queue delay for
     *   the [target]. By default, this matches [hitDelay]. When the magic hit "splashes," this is
     *   often set to `1` to trigger faster retaliation.
     */
    public fun queueMagicHit(
        source: Player,
        target: PathingEntity,
        spell: ObjType?,
        damage: Int,
        clientDelay: Int,
        hitDelay: Int,
        retaliationDelay: Int = hitDelay,
    ): Hit =
        when (target) {
            is Npc ->
                queueMagicHit(
                    source = source,
                    target = target,
                    spell = spell,
                    damage = damage,
                    clientDelay = clientDelay,
                    hitDelay = hitDelay,
                    retaliationDelay = retaliationDelay,
                )
            is Player ->
                queueMagicHit(
                    source = source,
                    target = target,
                    spell = spell,
                    damage = damage,
                    clientDelay = clientDelay,
                    hitDelay = hitDelay,
                    retaliationDelay = retaliationDelay,
                )
        }

    private fun queueMagicHit(
        source: Player,
        target: Npc,
        spell: ObjType?,
        damage: Int,
        clientDelay: Int,
        hitDelay: Int,
        retaliationDelay: Int,
    ): Hit {
        // Note: Retaliation must be queued _before_ the hit. If queued after, every hit would
        // trigger the "speed-up" death mechanic, since the hit queues would no longer be the
        // last entries in the queue list at the time of processing.
        target.queueCombatRetaliate(source, retaliationDelay)

        val hit =
            target.queueHit(
                source = source,
                delay = hitDelay,
                type = HitType.Magic,
                damage = damage,
                modifier = npcHitModifier,
                sourceSecondary = spell,
            )
        target.heroPoints(source, min(hit.damage, target.hitpoints))
        target.combatPlayDefendAnim(clientDelay)
        return hit
    }

    private fun queueMagicHit(
        source: Player,
        target: Player,
        spell: ObjType?,
        damage: Int,
        clientDelay: Int,
        hitDelay: Int,
        retaliationDelay: Int,
    ): Hit {
        // Note: Retaliation must be queued _before_ the hit. If queued after, every hit would
        // trigger the "speed-up" death mechanic, since the hit queues would no longer be the
        // last entries in the queue list at the time of processing.
        target.queueCombatRetaliate(source, retaliationDelay)

        val hit =
            target.queueHit(
                source = source,
                delay = hitDelay,
                type = HitType.Magic,
                damage = damage,
                sourceSecondary = spell,
            )
        target.heroPoints(source, min(hit.damage, target.hitpoints))
        target.combatPlayDefendAnim(objTypes, clientDelay)
        return hit
    }

    /**
     * Queues a magic **splash** hit on [target], triggering auto-retaliation after 1 server cycle -
     * a quirk of how splashing works.
     *
     * This is equivalent to calling [queueMagicHit] with `damage = 0` and `retaliationDelay = 1`.
     *
     * **Note:** This function **does not** play splash sounds or visual effects on the [target].
     * These must be handled separately by calling [playMagicSplashFx].
     *
     * @see [PlayerAttackManager.queueMagicHit]
     */
    public fun queueSplashHit(
        source: Player,
        target: PathingEntity,
        spell: ObjType?,
        clientDelay: Int,
        hitDelay: Int,
    ): Hit =
        queueMagicHit(
            source = source,
            target = target,
            spell = spell,
            damage = 0,
            clientDelay = clientDelay,
            hitDelay = hitDelay,
            retaliationDelay = 1,
        )

    /**
     * Plays the visual and sound effects for non-splash magic hits.
     *
     * @param clientDelay The delay in client cycles (`20ms` per cycle) before the projectile
     *   visually lands on [target]. This is usually derived from the projectile's metadata and
     *   determines when the [hitSpot] spotanim should play on [target].
     * @param castSound The sound to play immediately. Plays as a `sound_area` if the target is a
     *   [Player], or a `sound_synth` if target is an [Npc]. If `null`, no cast sound is played.
     * @param soundRadius The radius to use for [castSound] when played as a `soundarea`.
     * @param hitSpot The spotanim to play on [target] after [clientDelay]. If `null`, no spotanim
     *   is shown.
     * @param hitSpotHeight The height to use for [hitSpot] when applicable.
     * @param hitSound The sound to play as a `soundarea` on [target]'s coords after [clientDelay].
     */
    public fun playMagicHitFx(
        source: Player,
        target: PathingEntity,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
        hitSpot: SpotanimType?,
        hitSpotHeight: Int,
        hitSound: SynthType?,
    ): Unit =
        when (target) {
            is Npc ->
                playMagicHitFx(
                    source = source,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                    hitSpot = hitSpot,
                    hitSpotHeight = hitSpotHeight,
                    hitSound = hitSound,
                )
            is Player ->
                playMagicHitFx(
                    source = source,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                    soundRadius = soundRadius,
                    hitSpot = hitSpot,
                    hitSpotHeight = hitSpotHeight,
                    hitSound = hitSound,
                )
        }

    private fun playMagicHitFx(
        source: Player,
        target: Npc,
        clientDelay: Int,
        castSound: SynthType?,
        hitSpot: SpotanimType?,
        hitSpotHeight: Int,
        hitSound: SynthType?,
    ) {
        if (castSound != null) {
            source.soundSynth(castSound)
        }

        if (hitSpot != null) {
            target.spotanim(hitSpot, delay = clientDelay, height = hitSpotHeight)
        }

        if (hitSound != null) {
            soundArea(
                source = target.coords,
                synth = hitSound,
                delay = clientDelay,
                loops = 1,
                radius = 10,
                size = 0,
            )
        }
    }

    private fun playMagicHitFx(
        source: Player,
        target: Player,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
        hitSpot: SpotanimType?,
        hitSpotHeight: Int,
        hitSound: SynthType?,
    ) {
        if (castSound != null) {
            soundArea(
                source = source.coords,
                synth = castSound,
                delay = 0,
                loops = 1,
                radius = soundRadius,
                size = 0,
            )
        }

        if (hitSpot != null) {
            target.spotanim(hitSpot, delay = clientDelay, height = hitSpotHeight)
        }

        if (hitSound != null) {
            soundArea(
                source = target.coords,
                synth = hitSound,
                delay = clientDelay,
                loops = 1,
                radius = 10,
                size = 0,
            )
        }
    }

    /**
     * Plays the visual and sound effects for magic **splash** hits.
     *
     * @param clientDelay The delay in client cycles (`20ms` per cycle) before the splash spotanim
     *   and sound are played on [target]. Typically derived from the projectile's metadata.
     * @param castSound The sound to play immediately. Plays as a `soundarea` if [target] is a
     *   [Player], or a `soundsynth` if [target] is an [Npc]. If `null`, no cast sound is played.
     * @param soundRadius The radius to use for [castSound] when played as a `soundarea`.
     */
    public fun playMagicSplashFx(
        source: Player,
        target: PathingEntity,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
    ): Unit =
        when (target) {
            is Npc ->
                playMagicSplashFx(
                    source = source,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                )
            is Player ->
                playMagicSplashFx(
                    source = source,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                    soundRadius = soundRadius,
                )
        }

    private fun playMagicSplashFx(
        source: Player,
        target: Npc,
        clientDelay: Int,
        castSound: SynthType?,
    ) {
        if (castSound != null) {
            source.soundSynth(castSound)
        }
        target.spotanim(spotanims.splash, delay = clientDelay, height = 124)
        soundArea(
            source = target.coords,
            synth = synths.spellfail,
            delay = clientDelay,
            loops = 1,
            radius = 10,
            size = 0,
        )
    }

    private fun playMagicSplashFx(
        source: Player,
        target: Player,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
    ) {
        if (castSound != null) {
            soundArea(
                source = source.coords,
                synth = castSound,
                delay = 0,
                loops = 1,
                radius = soundRadius,
                size = 0,
            )
        }
        target.spotanim(spotanims.splash, delay = clientDelay, height = 124)
        soundArea(
            source = target.coords,
            synth = synths.spellfail,
            delay = clientDelay,
            loops = 1,
            radius = 10,
            size = 0,
        )
    }

    public fun spawnProjectile(
        source: Player,
        target: PathingEntity,
        spotanim: SpotanimType,
        projanim: ProjAnimType,
    ): ProjAnim =
        when (target) {
            is Npc -> spawnProjectile(source, target, spotanim, projanim)
            is Player -> spawnProjectile(source, target, spotanim, projanim)
        }

    public fun spawnProjectile(
        source: Player,
        target: Npc,
        spotanim: SpotanimType,
        projanim: ProjAnimType,
    ): ProjAnim = worldRepo.projAnim(source, target, spotanim, projanim)

    public fun spawnProjectile(
        source: Player,
        target: Player,
        spotanim: SpotanimType,
        projanim: ProjAnimType,
    ): ProjAnim = worldRepo.projAnim(source, target, spotanim, projanim)

    public fun soundArea(
        source: CoordGrid,
        synth: SynthType,
        delay: Int,
        loops: Int,
        radius: Int,
        size: Int,
    ): Unit =
        worldRepo.soundArea(
            source,
            synth,
            delay = delay,
            loops = loops,
            radius = radius,
            size = size,
        )
}
