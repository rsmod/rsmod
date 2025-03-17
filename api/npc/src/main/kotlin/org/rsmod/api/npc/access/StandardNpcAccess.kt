package org.rsmod.api.npc.access

import kotlin.getValue
import kotlin.setValue
import org.rsmod.annotations.InternalApi
import org.rsmod.api.config.refs.BaseHitmarkGroups
import org.rsmod.api.config.refs.hitmark_groups
import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.modifier.StandardNpcHitModifier
import org.rsmod.api.npc.hit.process
import org.rsmod.api.npc.hit.processor.NpcHitProcessor
import org.rsmod.api.npc.hit.processor.StandardNpcHitProcessor
import org.rsmod.api.npc.hit.queueHit
import org.rsmod.api.npc.interact.AiPlayerInteractions
import org.rsmod.api.npc.opPlayer2
import org.rsmod.api.npc.queueDeath
import org.rsmod.api.random.GameRandom
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.util.PathingEntityCommon
import org.rsmod.game.hit.Hit
import org.rsmod.game.hit.HitType
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.map.collision.isZoneValid
import org.rsmod.game.type.hitmark.HitmarkTypeGroup
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.seq.SeqTypeList
import org.rsmod.game.type.seq.UnpackedSeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionFlagMap

/**
 * Manages scoped actions for npcs that implicitly launch a coroutine, allowing functions such as
 * `delay`.
 *
 * This system is similar to `ProtectedAccess` for players but differs in that it does not enforce
 * safeguards when launching this scope for the [npc]. Even if the [npc] already has an active
 * [coroutine] or if [Npc.isBusy] is `true`, a new [StandardNpcAccess] can still be invoked.
 */
public class StandardNpcAccess(
    public val npc: Npc,
    private val coroutine: GameCoroutine,
    private val context: StandardNpcAccessContext,
) {
    public val random: GameRandom by context::random

    public val coords: CoordGrid by npc::coords
    public val mapClock: Int by npc::currentMapClock

    public var actionDelay: Int by npc::actionDelay

    // TODO(combat): Go over the [Npc.walk] implementation. It should not work as it currently does,
    //  but some tests currently rely on it working as it does. Once we decide on that, replace this
    //  logic with `npc.walk`.
    public fun walk(dest: CoordGrid) {
        npc.abortRoute()
        npc.routeDestination.add(dest)
    }

    public suspend fun delay(cycles: Int = 1) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        npc.delay(cycles)
        coroutine.pause { npc.isNotDelayed }
    }

    public suspend fun arriveDelay() {
        if (!npc.hasMovedPreviousCycle) {
            return
        }
        delay()
    }

    /**
     * Delays the npc for a number of ticks equal to the time duration of [seq].
     *
     * @param seq The seq type whose tick duration determines the delay.
     * @throws IllegalStateException if [UnpackedSeqType.tickDuration] is `0`.
     */
    public suspend fun delay(seq: SeqType, seqTypes: SeqTypeList) {
        delay(seqTypes[seq])
    }

    /**
     * Delays the npc for a number of ticks equal to the time duration of [seq].
     *
     * @param seq The seq type whose tick duration determines the delay.
     * @throws IllegalStateException if [UnpackedSeqType.tickDuration] is `0`.
     */
    public suspend fun delay(seq: UnpackedSeqType) {
        val ticks = seq.tickDuration
        check(ticks > 0) { "Seq tick duration must be positive: $seq" }
        delay(cycles = ticks)
    }

    public fun telejump(dest: CoordGrid, collision: CollisionFlagMap) {
        if (!collision.isZoneValid(dest)) {
            // TODO: Decide if we want to silently-fail, log, or let `PathingEntityCommon.telejump`
            //  error, which will cause the `npc` to be deleted by the engine.
            return
        }
        PathingEntityCommon.telejump(npc, collision, dest)
    }

    public fun teleport(dest: CoordGrid, collision: CollisionFlagMap) {
        if (!collision.isZoneValid(dest)) {
            // TODO: Decide if we want to silently-fail, log, or let `PathingEntityCommon.teleport`
            //  error, which will cause the `npc` to be deleted by the engine.
            return
        }
        PathingEntityCommon.teleport(npc, collision, dest)
    }

    public fun queueDeath() {
        npc.queueDeath()
    }

    /**
     * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is
     * displayed and health is deducted from the npc.
     *
     * _[modifier] is applied immediately when this function is called (via
     * [NpcHitModifier.modify]). This means that effects like npc prayer protection reducing damage
     * are handled at this point and **not** on impact._
     *
     * **Notes:**
     * - [StandardNpcHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [npc]'s health, triggering the associated
     *   `onNpcHit` scripts, displaying the hitsplat, and handling other related mechanics.
     * - Though the hit is immediately modified, an accurate [Hit] representation of what will be
     *   dealt after the [delay] cycle **cannot be guaranteed**. This is because npc hit processing
     *   can further modify the hit in certain cases, such as when a npc is restricted from falling
     *   below a specific health threshold during a particular "phase." Be mindful of this when
     *   using the returned [Hit] instance.
     * - If you need an **accurate** [Hit] representation, use the [Hit] instance provided in
     *   `onNpcHit` scripts rather than the value returned by this function.
     *
     * @param damage The initial damage intended for the [npc]. This value may change based on
     *   various factors from [modifier] and [StandardNpcHitProcessor].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param sourceWeapon An optional [ObjType] reference of a "weapon" used by the [source] that
     *   hit modifiers and/or processors can use for specialized logic. Typically unnecessary when
     *   [source] is an [Npc], though there may be niche use cases.
     * @param sourceSecondary Similar to [sourceWeapon], except this refers to objs that are **not**
     *   the primary weapon, such as ammunition for ranged attacks or objs tied to magic spells.
     * @param modifier An [NpcHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardNpcHitModifier], which applies standard modifications,
     *   such as damage reduction from npc protection prayers.
     * @see [BaseHitmarkGroups]
     */
    public fun queueHit(
        source: Npc,
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        sourceWeapon: ObjType? = null,
        sourceSecondary: ObjType? = null,
        modifier: NpcHitModifier = context.hitModifier,
    ): Hit =
        npc.queueHit(
            source = source,
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            sourceWeapon = sourceWeapon,
            sourceSecondary = sourceSecondary,
            modifier = modifier,
        )

    /**
     * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is
     * displayed and health is deducted from the npc.
     *
     * _[modifier] is applied immediately when this function is called (via
     * [NpcHitModifier.modify]). This means that effects like npc prayer protection reducing damage
     * are handled at this point and **not** on impact._
     *
     * **Notes:**
     * - The [Hit.righthandObj] is implicitly set based on the `righthand` obj equipped in
     *   [Player.worn] for [source]. This behavior is not configurable to ensure consistency across
     *   systems such as [modifier] and other processors.
     * - [StandardNpcHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [npc]'s health, triggering the associated
     *   `onNpcHit` scripts, displaying the hitsplat, and handling other related mechanics.
     * - Though the hit is immediately modified, an accurate [Hit] representation of what will be
     *   dealt after the [delay] cycle **cannot be guaranteed**. This is because npc hit processing
     *   can further modify the hit in certain cases, such as when a npc is restricted from falling
     *   below a specific health threshold during a particular "phase." Be mindful of this when
     *   using the returned [Hit] instance.
     * - If you need an **accurate** [Hit] representation, use the [Hit] instance provided in
     *   `onNpcHit` scripts rather than the value returned by this function.
     *
     * @param damage The initial damage intended for the [npc]. This value may change based on
     *   various factors from [modifier] and [StandardNpcHitProcessor].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param specific If `true`, only [source] will see the hitsplat; this does not affect actual
     *   damage calculations.
     * @param sourceSecondary The "secondary" obj used in the attack by [source]. If the hit is from
     *   a ranged attack, this should be set to the ammunition obj (if applicable). If the attack is
     *   from a magic spell, this should be the associated spell obj.
     * @param modifier An [NpcHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardNpcHitModifier], which applies standard modifications,
     *   such as damage reduction from npc protection prayers.
     * @see [BaseHitmarkGroups]
     */
    public fun queueHit(
        source: Player,
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        specific: Boolean = false,
        sourceSecondary: ObjType? = null,
        modifier: NpcHitModifier = context.hitModifier,
    ): Hit =
        npc.queueHit(
            source = source,
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            specific = specific,
            sourceSecondary = sourceSecondary,
            modifier = modifier,
        )

    /**
     * Queues a hit that does not originate from either a [Player] or an [Npc], with an impact cycle
     * delay of [delay] before the hit is displayed and health is deducted from the npc.
     *
     * _[modifier] is applied immediately when this function is called (via
     * [NpcHitModifier.modify]). This means that effects like npc prayer protection reducing damage
     * are handled at this point and **not** on impact._
     *
     * **Notes:**
     * - [StandardNpcHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [npc]'s health, triggering the associated
     *   `onNpcHit` scripts, displaying the hitsplat, and handling other related mechanics.
     * - Though the hit is immediately modified, an accurate [Hit] representation of what will be
     *   dealt after the [delay] cycle **cannot be guaranteed**. This is because npc hit processing
     *   can further modify the hit in certain cases, such as when a npc is restricted from falling
     *   below a specific health threshold during a particular "phase." Be mindful of this when
     *   using the returned [Hit] instance.
     * - If you need an **accurate** [Hit] representation, use the [Hit] instance provided in
     *   `onNpcHit` scripts rather than the value returned by this function.
     *
     * @param damage The initial damage intended for the [npc]. This value may change based on
     *   various factors from [modifier] and [StandardNpcHitProcessor].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param modifier An [NpcHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardNpcHitModifier], which applies standard modifications,
     *   such as damage reduction from npc protection prayers.
     * @see [BaseHitmarkGroups]
     */
    public fun queueHit(
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        modifier: NpcHitModifier = context.hitModifier,
    ): Hit =
        npc.queueHit(
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            modifier = modifier,
        )

    @InternalApi
    public fun processQueuedHit(hit: Hit, processor: NpcHitProcessor = context.hitProcessor): Unit =
        processor.process(this, hit)

    public fun aiTimer(cycles: Int) {
        npc.aiTimer(cycles)
    }

    public fun timer(timerType: TimerType, cycles: Int) {
        npc.timer(timerType, cycles)
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        npc.queue(queue, cycles, args)
    }

    public fun clearQueue(queue: QueueType) {
        npc.clearQueue(queue)
    }

    /**
     * Adds "hero points" (also known as kill credits) for [source], where [points] typically
     * represent the amount of damage dealt to [npc].
     */
    public fun heroPoints(source: Player, points: Int) {
        npc.heroPoints(source, points)
    }

    /**
     * Finds the player with the highest "hero points" stored in this [Npc.heroPoints].
     *
     * **Notes:**
     * - Only players who have dealt damage greater than `0` can occupy an entry.
     * - [Npc.heroPoints] is limited to `16` entries by default. Once all entries are occupied, no
     *   additional players can accrue kill credit (hero points) for this [npc] until its hero
     *   points are cleared.
     * - Npcs can override the default hero points entry limit by modifying their
     *   [UnpackedNpcType.heroCount].
     */
    public fun findHero(playerList: PlayerList): Player? {
        return npc.findHero(playerList)
    }

    public fun say(text: String) {
        npc.say(text)
    }

    public fun resetMode() {
        npc.resetMode()
    }

    public fun defaultMode() {
        npc.defaultMode()
    }

    public fun noneMode() {
        npc.noneMode()
    }

    public fun playerEscape(target: Player) {
        npc.playerEscape(target)
    }

    public fun playerFaceClose(target: Player) {
        npc.playerFaceClose(target)
    }

    public fun playerFace(target: Player) {
        npc.playerFace(target)
    }

    public fun opPlayer2(target: Player, interactions: AiPlayerInteractions) {
        npc.opPlayer2(target, interactions)
    }

    public fun anim(seq: SeqType, delay: Int = 0) {
        npc.anim(seq, delay)
    }

    public fun resetAnim() {
        npc.resetAnim()
    }

    public fun animProtect(animProtect: Boolean) {
        PathingEntityCommon.setAnimProtect(npc, animProtect)
    }

    public fun resetSpotanim() {
        npc.resetSpotanim()
    }

    public fun spotanim(spot: SpotanimType, delay: Int = 0, height: Int = 0, slot: Int = 0) {
        npc.spotanim(spot, delay, height, slot)
    }

    @OptIn(InternalApi::class)
    public fun transmog(type: NpcType, typeList: NpcTypeList) {
        npc.transmog(typeList[type])
        npc.assignUid()
    }

    @OptIn(InternalApi::class)
    public fun transmog(type: UnpackedNpcType) {
        npc.transmog(type)
        npc.assignUid()
    }

    @OptIn(InternalApi::class)
    public fun resetTransmog() {
        npc.resetTransmog()
        npc.assignUid()
    }

    public fun isWithinDistance(
        target: CoordGrid,
        distance: Int,
        width: Int = 1,
        length: Int = 1,
    ): Boolean = npc.isWithinDistance(target, distance, width, length)

    public fun isWithinDistance(other: PathingEntity, distance: Int): Boolean =
        npc.isWithinDistance(other, distance)

    public fun isWithinDistance(loc: BoundLocInfo, distance: Int): Boolean =
        npc.isWithinDistance(loc, distance)

    public fun isWithinArea(southWest: CoordGrid, northEast: CoordGrid): Boolean =
        npc.isWithinArea(southWest, northEast)

    public fun distanceTo(target: CoordGrid, width: Int = 1, length: Int = 1): Int =
        npc.distanceTo(target, width, length)

    public fun distanceTo(other: PathingEntity): Int = npc.distanceTo(other)

    public fun distanceTo(loc: BoundLocInfo): Int = npc.distanceTo(loc)

    public fun apRange(distance: Int) {
        val interaction = npc.interaction ?: return
        interaction.apRange = distance
        interaction.apRangeCalled = true
    }

    public fun isWithinApRange(loc: BoundLocInfo, distance: Int): Boolean {
        if (!isWithinDistance(loc, distance)) {
            apRange(distance)
            return false
        }
        return true
    }

    public fun isWithinApRange(target: PathingEntity, distance: Int): Boolean {
        if (!isWithinDistance(target, distance)) {
            apRange(distance)
            return false
        }
        return true
    }

    /**
     * Returns the param value associated with [param] from the **base** `npc` [Npc.type], or `null`
     * if the type does not have a value associated with [param] and [param] does not have a
     * non-null `default` value.
     *
     * If you wish to retrieve the param value for the current (transmog) type, use [Npc.visType] to
     * retrieve it.
     */
    public fun <T : Any> paramOrNull(param: ParamType<T>): T? = npc.paramOrNull(param)

    /**
     * Returns the param value associated with [param] from the **base** npc` [Npc.type].
     *
     * If you wish to retrieve the param value for the current (transmog) type, use [Npc.visType] to
     * retrieve it.
     *
     * @throws IllegalStateException if the type does not have a value associated with [param] and
     *   [param] does not have a non-null `default` value.
     */
    public fun <T : Any> param(param: ParamType<T>): T = npc.param(param)

    override fun toString(): String = "StandardNpcAccess(npc=$npc, coroutine=$coroutine)"
}
