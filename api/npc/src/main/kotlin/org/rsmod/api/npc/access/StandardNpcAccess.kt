package org.rsmod.api.npc.access

import kotlin.getValue
import org.rsmod.api.random.GameRandom
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.shared.PathingEntityCommon
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.map.collision.isZoneValid
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.SeqType
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

    public fun aiTimer(cycles: Int) {
        npc.aiTimer(cycles)
    }

    public fun timer(timerType: TimerType, cycles: Int) {
        npc.timer(timerType, cycles)
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        npc.queue(queue, cycles, args)
    }

    public fun say(text: String) {
        npc.say(text)
    }

    public fun resetMode() {
        npc.resetMode()
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

    public fun transmog(type: NpcType, typeList: NpcTypeList) {
        npc.transmog(typeList[type])
    }

    public fun transmog(type: UnpackedNpcType) {
        npc.transmog(type)
    }

    public fun resetTransmog() {
        npc.resetTransmog()
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

    override fun toString(): String = "StandardNpcAccess(npc=$npc, coroutine=$coroutine)"
}
