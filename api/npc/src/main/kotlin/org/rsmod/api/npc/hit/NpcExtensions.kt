package org.rsmod.api.npc.hit

import org.rsmod.api.config.refs.BaseHitmarkGroups
import org.rsmod.api.config.refs.hitmarks
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.hit.configs.hit_queues
import org.rsmod.api.npc.hit.modifier.HitModifierNpc
import org.rsmod.api.npc.hit.modifier.StandardNpcHitModifier
import org.rsmod.api.npc.hit.processor.QueuedNpcHitProcessor
import org.rsmod.api.npc.hit.processor.StandardNpcHitProcessor
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
 * and health is deducted from the npc.
 *
 * _[modifier] is applied immediately when this function is called (via [HitModifierNpc.modify]).
 * This means that effects like npc prayer protection reducing damage are handled at this point and
 * **not** on impact._
 *
 * **Notes:**
 * - [StandardNpcHitProcessor] is invoked when the cycle [delay] completes and the hit takes effect.
 *   It is responsible for reducing this [Npc]'s health, triggering the associated `onNpcHit`
 *   scripts, displaying the hitsplat, and handling other related mechanics.
 * - Though the hit is immediately modified, an accurate [Hit] representation of what will be dealt
 *   after the [delay] cycle **cannot be guaranteed**. This is because npc hit processing can
 *   further modify the hit in certain cases, such as when a npc is restricted from falling below a
 *   specific health threshold during a particular "phase." Be mindful of this when using the
 *   returned [Hit] instance.
 * - If you need an **accurate** [Hit] representation, use the [Hit] instance provided in `onNpcHit`
 *   scripts rather than the value returned by this function.
 *
 * @param damage The initial damage intended for this [Npc]. This value may change based on various
 *   factors from [modifier] and [StandardNpcHitProcessor].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmarks] for a list of available hitmark groups.
 * @param sourceWeapon An optional [ObjType] reference of a "weapon" used by the [source] that hit
 *   modifiers and/or processors can use for specialized logic. Typically unnecessary when [source]
 *   is an [Npc], though there may be niche use cases.
 * @param sourceSecondary Similar to [sourceWeapon], except this refers to objs that are **not** the
 *   primary weapon, such as ammunition for ranged attacks or objs tied to magic spells.
 * @param modifier A [HitModifierNpc] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardNpcHitModifier], which applies standard modifications, such as damage
 *   reduction from npc protection prayers.
 * @see [BaseHitmarkGroups]
 */
public fun Npc.queueHit(
    source: Npc,
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmarks.regular_damage,
    sourceWeapon: ObjType? = null,
    sourceSecondary: ObjType? = null,
    modifier: HitModifierNpc = StandardNpcHitModifier,
): Hit {
    val builder =
        InternalNpcHits.createBuilder(
            source = source,
            type = type,
            damage = damage,
            righthand = sourceWeapon,
            secondaryObj = sourceSecondary,
            hitmark = hitmark,
            clientDelay = 0,
        )
    return modifyAndQueueHit(delay, builder, modifier)
}

/**
 * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is displayed
 * and health is deducted from the npc.
 *
 * _[modifier] is applied immediately when this function is called (via [HitModifierNpc.modify]).
 * This means that effects like npc prayer protection reducing damage are handled at this point and
 * **not** on impact._
 *
 * **Notes:**
 * - The [Hit.righthandObj] is implicitly set based on the `righthand` obj equipped in [Player.worn]
 *   for [source]. This behavior is not configurable to ensure consistency across systems such as
 *   [modifier] and other processors.
 * - [StandardNpcHitProcessor] is invoked when the cycle [delay] completes and the hit takes effect.
 *   It is responsible for reducing this [Npc]'s health, triggering the associated `onNpcHit`
 *   scripts, displaying the hitsplat, and handling other related mechanics.
 * - Though the hit is immediately modified, an accurate [Hit] representation of what will be dealt
 *   after the [delay] cycle **cannot be guaranteed**. This is because npc hit processing can
 *   further modify the hit in certain cases, such as when a npc is restricted from falling below a
 *   specific health threshold during a particular "phase." Be mindful of this when using the
 *   returned [Hit] instance.
 * - If you need an **accurate** [Hit] representation, use the [Hit] instance provided in `onNpcHit`
 *   scripts rather than the value returned by this function.
 *
 * @param damage The initial damage intended for this [Npc]. This value may change based on various
 *   factors from [modifier] and [StandardNpcHitProcessor].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmarks] for a list of available hitmark groups.
 * @param specific If `true`, only [source] will see the hitsplat; this does not affect actual
 *   damage calculations.
 * @param sourceSecondary The "secondary" obj used in the attack by [source]. If the hit is from a
 *   ranged attack, this should be set to the ammunition obj (if applicable). If the attack is from
 *   a magic spell, this should be the associated spell obj.
 * @param modifier A [HitModifierNpc] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardNpcHitModifier], which applies standard modifications, such as damage
 *   reduction from npc protection prayers.
 * @see [BaseHitmarkGroups]
 */
public fun Npc.queueHit(
    source: Player,
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmarks.regular_damage,
    specific: Boolean = false,
    sourceSecondary: ObjType? = null,
    modifier: HitModifierNpc = StandardNpcHitModifier,
): Hit {
    val builder =
        InternalNpcHits.createBuilder(
            source = source,
            type = type,
            damage = damage,
            secondaryObj = sourceSecondary,
            hitmark = hitmark,
            clientDelay = 0,
            specific = specific,
        )
    return modifyAndQueueHit(delay, builder, modifier)
}

/**
 * Queues a hit that does not originate from either a [Player] or an [Npc], with an impact cycle
 * delay of [delay] before the hit is displayed and health is deducted from the npc.
 *
 * _[modifier] is applied immediately when this function is called (via [HitModifierNpc.modify]).
 * This means that effects like npc prayer protection reducing damage are handled at this point and
 * **not** on impact._
 *
 * **Notes:**
 * - [StandardNpcHitProcessor] is invoked when the cycle [delay] completes and the hit takes effect.
 *   It is responsible for reducing this [Npc]'s health, triggering the associated `onNpcHit`
 *   scripts, displaying the hitsplat, and handling other related mechanics.
 * - Though the hit is immediately modified, an accurate [Hit] representation of what will be dealt
 *   after the [delay] cycle **cannot be guaranteed**. This is because npc hit processing can
 *   further modify the hit in certain cases, such as when a npc is restricted from falling below a
 *   specific health threshold during a particular "phase." Be mindful of this when using the
 *   returned [Hit] instance.
 * - If you need an **accurate** [Hit] representation, use the [Hit] instance provided in `onNpcHit`
 *   scripts rather than the value returned by this function.
 *
 * @param damage The initial damage intended for this [Npc]. This value may change based on various
 *   factors from [modifier] and [StandardNpcHitProcessor].
 * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
 *   reference [hitmarks] for a list of available hitmark groups.
 * @param modifier A [HitModifierNpc] used to adjust damage and other hit properties. By default,
 *   this is set to [StandardNpcHitModifier], which applies standard modifications, such as damage
 *   reduction from npc protection prayers.
 * @see [BaseHitmarkGroups]
 */
public fun Npc.queueHit(
    delay: Int,
    type: HitType,
    damage: Int,
    hitmark: HitmarkTypeGroup = hitmarks.regular_damage,
    modifier: HitModifierNpc = StandardNpcHitModifier,
): Hit {
    val builder =
        InternalNpcHits.createBuilder(
            type = type,
            damage = damage,
            righthand = null,
            secondaryObj = null,
            hitmark = hitmark,
            clientDelay = 0,
            specific = false,
        )
    return modifyAndQueueHit(delay, builder, modifier)
}

private fun Npc.modifyAndQueueHit(delay: Int, builder: HitBuilder, modifier: HitModifierNpc): Hit {
    modifier.modify(builder, this)
    val hit = builder.build()
    queue(hit_queues.standard, delay, hit)
    return hit
}

/* Hit modifier helper functions. */
public fun HitModifierNpc.modify(builder: HitBuilder, target: Npc): Unit = builder.modify(target)

/* Hit processor helper functions. */
internal fun QueuedNpcHitProcessor.process(access: StandardNpcAccess, hit: Hit) {
    access.process(hit)
}
