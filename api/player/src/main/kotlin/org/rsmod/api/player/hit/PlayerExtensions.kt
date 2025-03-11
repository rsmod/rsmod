package org.rsmod.api.player.hit

import kotlin.math.min
import org.rsmod.api.config.refs.BaseHitmarkGroups
import org.rsmod.api.config.refs.hitmark_groups
import org.rsmod.api.player.hit.configs.hit_queues
import org.rsmod.api.player.hit.modifier.NoopPlayerHitModifier
import org.rsmod.api.player.hit.modifier.PlayerHitModifier
import org.rsmod.api.player.hit.modifier.StandardPlayerHitModifier
import org.rsmod.api.player.hit.processor.DamageOnlyPlayerHitProcessor
import org.rsmod.api.player.hit.processor.InstantPlayerHitProcessor
import org.rsmod.api.player.hit.processor.QueuedPlayerHitProcessor
import org.rsmod.api.player.hit.processor.StandardPlayerHitProcessor
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.Hit
import org.rsmod.game.hit.HitBuilder
import org.rsmod.game.hit.HitType
import org.rsmod.game.type.hitmark.HitmarkTypeGroup
import org.rsmod.game.type.obj.ObjType

/* Standard hit functions. */
/**
 * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is displayed
 * and health is deducted from the player.
 *
 * _[modifier] is applied immediately when this function is called (via [PlayerHitModifier.modify]).
 * This means that effects like prayer protection reducing damage are handled at this point and
 * **not** on impact._
 *
 * If you want the modifier to be applied on impact, use [queueImpactHit] instead.
 *
 * **Notes:**
 * - [damage] is capped to this [Player]'s current health at the time this function is called. This
 *   ensures that the "tick-eating" mechanic is possible.
 * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
 *   effect. It is responsible for reducing ths [Player]'s health, handling armour degradation,
 *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot be
 *   bypassed** when using this function; however, it can be changed when using [takeInstantHit].
 * - As the hit is immediately modified, this function **returns an accurate** [Hit] representation
 *   of what will be dealt once the cycle [delay] passes. The only exception is if this [Player]'s
 *   respective queue list is cleared, which would remove the hit before it has been processed.
 *
 * @param damage The initial damage intended for this [Player]. This value may change based on
 *   various factors from [modifier].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmark_groups] for a list of available hitmark groups.
 * @param specific If `true`, only this [Player] will see the hitsplat; this does not affect actual
 *   damage calculations.
 * @param sourceWeapon An optional [ObjType] reference of a "weapon" used by the [source] that hit
 *   modifiers and/or processors can use for specialized logic. Typically unnecessary when [source]
 *   is an [Npc], though there may be niche use cases.
 * @param sourceSecondary Similar to [sourceWeapon], except this refers to objs that are **not** the
 *   primary weapon, such as ammunition for ranged attacks or objs tied to magic spells.
 * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardPlayerHitModifier], which applies standard modifications, such as
 *   damage reduction from protection prayers.
 * @see [BaseHitmarkGroups]
 */
public fun Player.queueHit(
    source: Npc,
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
    specific: Boolean = false,
    sourceWeapon: ObjType? = null,
    sourceSecondary: ObjType? = null,
    modifier: PlayerHitModifier = StandardPlayerHitModifier,
): Hit {
    val cappedDamage = min(hitpoints, damage)
    val builder =
        InternalPlayerHits.createBuilder(
            source = source,
            type = type,
            damage = cappedDamage,
            righthand = sourceWeapon,
            secondaryObj = sourceSecondary,
            hitmark = hitmark,
            clientDelay = 0,
            specific = specific,
        )
    return modifyAndStrongQueueHit(delay, builder, modifier)
}

/**
 * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is displayed
 * and health is deducted from the player.
 *
 * _[modifier] is applied immediately when this function is called (via [PlayerHitModifier.modify]).
 * This means that effects like prayer protection reducing damage are handled at this point and
 * **not** on impact._
 *
 * If you want the modifier to be applied on impact, use [queueImpactHit] instead.
 *
 * **Notes:**
 * - The [Hit.righthandObj] is implicitly set based on the `righthand` obj equipped in [Player.worn]
 *   for [source]. This behavior is not configurable to ensure consistency across systems such as
 *   [modifier] and other processors.
 * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
 *   effect. It is responsible for reducing this [Player]'s health, handling armour degradation,
 *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot be
 *   bypassed** when using this function; however, it can be changed when using [takeInstantHit].
 * - As the hit is immediately modified, this function **returns an accurate** [Hit] representation
 *   of what will be dealt once the cycle [delay] passes. The only exception is if this [Player]'s
 *   respective queue list is cleared, which would remove the hit before it has been processed.
 *
 * @param damage The initial damage intended for this [Player]. This value may change based on
 *   various factors from [modifier].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmark_groups] for a list of available hitmark groups.
 * @param sourceSecondary The "secondary" obj used in the attack by [source]. If the hit is from a
 *   ranged attack, this should be set to the ammunition obj (if applicable). If the attack is from
 *   a magic spell, this should be the associated spell obj.
 * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardPlayerHitModifier], which applies standard modifications, such as
 *   damage reduction from protection prayers.
 * @see [BaseHitmarkGroups]
 */
public fun Player.queueHit(
    source: Player,
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
    sourceSecondary: ObjType? = null,
    modifier: PlayerHitModifier = StandardPlayerHitModifier,
): Hit {
    val builder =
        InternalPlayerHits.createBuilder(
            target = this,
            source = source,
            type = type,
            damage = damage,
            secondaryObj = sourceSecondary,
            hitmark = hitmark,
            clientDelay = 0,
            specific = false,
        )
    return modifyAndStrongQueueHit(delay, builder, modifier)
}

/**
 * Queues a hit that does not originate from either a [Player] or an [Npc], with an impact cycle
 * delay of [delay] before the hit is displayed and health is deducted from the player.
 *
 * _[modifier] is applied immediately when this function is called (via [PlayerHitModifier.modify]).
 * This means that effects like prayer protection reducing damage are handled at this point and
 * **not** on impact._
 *
 * If you want the modifier to be applied on impact, use [queueImpactHit] instead.
 *
 * **Notes:**
 * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
 *   effect. It is responsible for reducing this [Player]'s health, handling armour degradation,
 *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot be
 *   bypassed** when using this function; however, it can be changed when using [takeInstantHit].
 * - As the hit is immediately modified, this function **returns an accurate** [Hit] representation
 *   of what will be dealt once the cycle [delay] passes. The only exception is if this [Player]'s
 *   respective queue list is cleared, which would remove the hit before it has been processed.
 *
 * @param damage The initial damage intended for this [Player]. This value may change based on
 *   various factors from [modifier].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmark_groups] for a list of available hitmark groups.
 * @param specific If `true`, only this [Player] will see the hitsplat; this does not affect actual
 *   damage calculations.
 * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardPlayerHitModifier], which applies standard modifications, such as
 *   damage reduction from protection prayers.
 * @param strongQueue If `false`, the hit will be queued through [Player.queue] instead of
 *   [Player.strongQueue]. This is `true` by default. Currently, the only known case for setting
 *   this to `false` is for 'Ring of Recoil' damage, but other use cases may exist.
 * @see [BaseHitmarkGroups]
 */
public fun Player.queueHit(
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
    specific: Boolean = false,
    modifier: PlayerHitModifier = StandardPlayerHitModifier,
    strongQueue: Boolean = true,
): Hit {
    val builder =
        InternalPlayerHits.createBuilder(
            type = type,
            damage = damage,
            righthand = null,
            secondaryObj = null,
            hitmark = hitmark,
            clientDelay = 0,
            specific = specific,
        )
    return if (strongQueue) {
        modifyAndStrongQueueHit(delay, builder, modifier)
    } else {
        modifyAndQueueHit(delay, builder, modifier)
    }
}

private fun Player.modifyAndStrongQueueHit(
    delay: Int,
    builder: HitBuilder,
    modifier: PlayerHitModifier,
): Hit {
    modifier.modify(builder, this)
    val hit = builder.build()
    strongQueue(hit_queues.standard, delay, hit)
    return hit
}

private fun Player.modifyAndQueueHit(
    delay: Int,
    builder: HitBuilder,
    modifier: PlayerHitModifier,
): Hit {
    modifier.modify(builder, this)
    val hit = builder.build()
    queue(hit_queues.standard, delay, hit)
    return hit
}

/* "Instant" hit functions. */
/**
 * Instantly applies [damage] to this [Player]. By default, this function applies no modification to
 * the hit ([NoopPlayerHitModifier]) unless explicitly provided through [modifier].
 *
 * @param damage The initial damage intended for this [Player]. This value may be adjusted by
 *   [modifier] based on various factors.
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmark_groups] for a list of available hitmark groups.
 * @param specific If `true`, only this [Player] will see the hitsplat; this does not affect actual
 *   damage calculations.
 * @param modifier A [PlayerHitModifier] that modifies the damage and other properties.
 * @param processor A [InstantPlayerHitProcessor] that processes the [Hit] instantly. Defaults to
 *   [DamageOnlyPlayerHitProcessor], meaning effects such as degradation and recoil damage **will
 *   not** apply.
 */
public fun Player.takeInstantHit(
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
    specific: Boolean = false,
    modifier: PlayerHitModifier = NoopPlayerHitModifier,
    processor: InstantPlayerHitProcessor = DamageOnlyPlayerHitProcessor,
): Hit {
    val builder =
        InternalPlayerHits.createBuilder(
            type = type,
            damage = damage,
            righthand = null,
            secondaryObj = null,
            hitmark = hitmark,
            clientDelay = 0,
            specific = specific,
        )

    modifier.modify(builder, this)

    val hit = builder.build()
    processor.process(this, hit)
    return hit
}

/* "Impact" hit functions. */
/**
 * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is displayed
 * and health is deducted from the player.
 *
 * _[modifier] is applied **on impact** (via [PlayerHitModifier.modify]). This means that effects
 * like prayer protection reducing damage are handled right before the hit damage is reduced from
 * the player's health._
 *
 * If you want to apply the modifier as soon as the hit is queued, use [queueHit] instead.
 *
 * **Notes:**
 * - [damage] is **not** capped based on this [Player]'s current health.
 * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
 *   effect. It is responsible for reducing this [Player]'s health, handling armour degradation,
 *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot be
 *   bypassed** when using this function; however, it can be changed when using [takeInstantHit].
 * - Unlike the [queueHit] variants, this function **cannot** return an accurate [Hit]
 *   representation (and thus does not return one at all). This is because the hit is scheduled for
 *   modification only _after_ [delay] cycles have passed, and the only way to retrieve the modified
 *   value would be by suspending execution - something we do not do here for multiple reasons.
 *
 * @param damage The initial damage intended for this [Player]. This value may change based on
 *   various factors from [modifier].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmark_groups] for a list of available hitmark groups.
 * @param specific If `true`, only this [Player] will see the hitsplat; this does not affect actual
 *   damage calculations.
 * @param sourceWeapon An optional [ObjType] reference of a "weapon" used by the [source] that hit
 *   modifiers and/or processors can use for specialized logic. Typically unnecessary when [source]
 *   is an [Npc], though there may be niche use cases.
 * @param sourceSecondary Similar to [sourceWeapon], except this refers to objs that are **not** the
 *   primary weapon, such as ammunition for ranged attacks or objs tied to magic spells.
 * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardPlayerHitModifier], which applies standard modifications, such as
 *   damage reduction from protection prayers.
 * @see [BaseHitmarkGroups]
 */
public fun Player.queueImpactHit(
    source: Npc,
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
    specific: Boolean = false,
    sourceWeapon: ObjType? = null,
    sourceSecondary: ObjType? = null,
    modifier: PlayerHitModifier = StandardPlayerHitModifier,
) {
    val builder =
        InternalPlayerHits.createBuilder(
            source = source,
            type = type,
            damage = damage,
            righthand = sourceWeapon,
            secondaryObj = sourceSecondary,
            hitmark = hitmark,
            clientDelay = 0,
            specific = specific,
        )
    strongQueueImpactHit(delay, builder, modifier)
}

/**
 * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is displayed
 * and health is deducted from the player.
 *
 * _[modifier] is applied **on impact** (via [PlayerHitModifier.modify]). This means that effects
 * like prayer protection reducing damage are handled right before the hit damage is reduced from
 * the player's health._
 *
 * If you want to apply the modifier as soon as the hit is queued, use [queueHit] instead.
 *
 * **Notes:**
 * - The [Hit.righthandObj] is implicitly set based on the `righthand` obj equipped in [Player.worn]
 *   for [source]. This behavior is not configurable to ensure consistency across systems such as
 *   [modifier] and other processors.
 * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
 *   effect. It is responsible for reducing this [Player]'s health, handling armour degradation,
 *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot be
 *   bypassed** when using this function; however, it can be changed when using [takeInstantHit].
 * - Unlike the [queueHit] variants, this function **cannot** return an accurate [Hit]
 *   representation (and thus does not return one at all). This is because the hit is scheduled for
 *   modification only _after_ [delay] cycles have passed, and the only way to retrieve the modified
 *   value would be by suspending execution - something we do not do here for multiple reasons.
 *
 * @param damage The initial damage intended for this [Player]. This value may change based on
 *   various factors from [modifier].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmark_groups] for a list of available hitmark groups.
 * @param sourceSecondary The "secondary" obj used in the attack by [source]. If the hit is from a
 *   ranged attack, this should be set to the ammunition obj (if applicable). If the attack is from
 *   a magic spell, this should be the associated spell obj.
 * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardPlayerHitModifier], which applies standard modifications, such as
 *   damage reduction from protection prayers.
 * @see [BaseHitmarkGroups]
 */
public fun Player.queueImpactHit(
    source: Player,
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
    sourceSecondary: ObjType? = null,
    modifier: PlayerHitModifier = StandardPlayerHitModifier,
) {
    val builder =
        InternalPlayerHits.createBuilder(
            target = this,
            source = source,
            type = type,
            damage = damage,
            secondaryObj = sourceSecondary,
            hitmark = hitmark,
            clientDelay = 0,
            specific = false,
        )
    strongQueueImpactHit(delay, builder, modifier)
}

/**
 * Queues a hit that does not originate from either a [Player] or an [Npc], with an impact cycle
 * delay of [delay] before the hit is displayed and health is deducted from the player.
 *
 * _[modifier] is applied **on impact** (via [PlayerHitModifier.modify]). This means that effects
 * like prayer protection reducing damage are handled right before the hit damage is reduced from
 * the player's health._
 *
 * If you want to apply the modifier as soon as the hit is queued, use [queueHit] instead.
 *
 * **Notes:**
 * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
 *   effect. It is responsible for reducing this [Player]'s health, handling armour degradation,
 *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot be
 *   bypassed** when using this function; however, it can be changed when using [takeInstantHit].
 * - Unlike the [queueHit] variants, this function **cannot** return an accurate [Hit]
 *   representation (and thus does not return one at all). This is because the hit is scheduled for
 *   modification only _after_ [delay] cycles have passed, and the only way to retrieve the modified
 *   value would be by suspending execution - something we do not do here for multiple reasons.
 *
 * @param damage The initial damage intended for this [Player]. This value may change based on
 *   various factors from [modifier].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmark_groups] for a list of available hitmark groups.
 * @param specific If `true`, only this [Player] will see the hitsplat; this does not affect actual
 *   damage calculations.
 * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardPlayerHitModifier], which applies standard modifications, such as
 *   damage reduction from protection prayers.
 * @see [BaseHitmarkGroups]
 */
public fun Player.queueImpactHit(
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
    specific: Boolean = false,
    modifier: PlayerHitModifier = StandardPlayerHitModifier,
    strongQueue: Boolean = true,
) {
    val builder =
        InternalPlayerHits.createBuilder(
            type = type,
            damage = damage,
            righthand = null,
            secondaryObj = null,
            hitmark = hitmark,
            clientDelay = 0,
            specific = specific,
        )
    if (strongQueue) {
        strongQueueImpactHit(delay, builder, modifier)
    } else {
        queueImpactHit(delay, builder, modifier)
    }
}

private fun Player.strongQueueImpactHit(
    delay: Int,
    builder: HitBuilder,
    modifier: PlayerHitModifier,
) {
    val deferred = DeferredPlayerHit(builder, modifier)
    strongQueue(hit_queues.impact, delay, deferred)
}

private fun Player.queueImpactHit(delay: Int, builder: HitBuilder, modifier: PlayerHitModifier) {
    val deferred = DeferredPlayerHit(builder, modifier)
    queue(hit_queues.impact, delay, deferred)
}

/* Internal functions. */
internal fun ProtectedAccess.processQueuedHit(hit: Hit, processor: QueuedPlayerHitProcessor) {
    processor.process(this, hit)
}

internal fun ProtectedAccess.processQueuedHit(
    builder: HitBuilder,
    modifier: PlayerHitModifier,
    processor: QueuedPlayerHitProcessor,
) {
    modifier.modify(builder, player)

    val hit = builder.build()
    processQueuedHit(hit, processor)
}

/* Hit modifier helper functions. */
public fun PlayerHitModifier.modify(builder: HitBuilder, target: Player): Unit =
    builder.modify(target)

/* Hit processor helper functions. */
private fun QueuedPlayerHitProcessor.process(access: ProtectedAccess, hit: Hit) {
    access.process(hit)
}

private fun InstantPlayerHitProcessor.process(target: Player, hit: Hit) {
    target.process(hit)
}
