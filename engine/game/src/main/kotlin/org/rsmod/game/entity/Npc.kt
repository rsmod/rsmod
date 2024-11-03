package org.rsmod.game.entity

import org.rsmod.coroutine.GameCoroutine
import org.rsmod.game.entity.npc.NpcInfoProtocol
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.shared.PathingEntityCommon
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.BlockWalk
import org.rsmod.game.movement.MoveRestrict
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.queue.NpcQueueList
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.timer.NpcTimerMap
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.collision.CollisionStrategy

public class Npc(
    public val type: UnpackedNpcType,
    override val avatar: NpcAvatar = NpcAvatar(type.size),
) : PathingEntity() {
    public constructor(type: UnpackedNpcType, coords: CoordGrid) : this(type) {
        this.coords = coords
        this.spawnCoords = coords
    }

    override val isBusy: Boolean
        get() = isDelayed

    override val blockWalkCollisionFlag: Int?
        get() = blockWalk.collisionFlag

    override val collisionStrategy: CollisionStrategy?
        get() = moveRestrict.collisionStrategy

    public val timerMap: NpcTimerMap = NpcTimerMap()
    public val queueList: NpcQueueList = NpcQueueList()

    public var spawnCoords: CoordGrid = coords
    public var defaultMoveSpeed: MoveSpeed = MoveSpeed.Walk

    public var mode: NpcMode? = type.defaultMode

    public var aiTimerStart: Int = type.timer
    public var aiTimer: Int = type.timer

    public var lifecycleAddCycle: Int = -1
    public var lifecycleDelCycle: Int = -1
    public var lifecycleRevealCycle: Int = -1

    public var patrolWaypointIndex: Int = 0
    public var patrolIdleCycles: Int = -1
    public var patrolPauseCycles: Int = 0

    public var wanderIdleCycles: Int = -1

    public val id: Int
        get() = type.id

    public val name: String
        get() = type.name

    public val moveRestrict: MoveRestrict
        get() = type.moveRestrict

    public val blockWalk: BlockWalk
        get() = type.blockWalk

    public val respawnDir: Direction
        get() = type.respawnDir

    public val wanderRange: Int
        get() = type.wanderRange

    public val defaultMode: NpcMode
        get() = type.defaultMode

    public var infoProtocol: NpcInfoProtocol
        get() = avatar.infoProtocol
        set(value) {
            avatar.infoProtocol = value
        }

    public fun walk(dest: CoordGrid): Unit = PathingEntityCommon.walk(this, dest)

    public fun teleport(collision: CollisionFlagMap, dest: CoordGrid): Unit =
        PathingEntityCommon.teleport(this, collision, dest)

    public fun telejump(collision: CollisionFlagMap, dest: CoordGrid): Unit =
        PathingEntityCommon.telejump(this, collision, dest)

    public fun aiTimer(cycles: Int) {
        this.aiTimerStart = cycles
        this.aiTimer = cycles
    }

    public fun timer(timer: TimerType, cycles: Int) {
        timerMap[timer] = currentMapClock + cycles
    }

    public fun queue(queue: QueueType, cycles: Int) {
        queueList.add(queue, cycles)
    }

    public fun resetMode() {
        resetFaceEntity()
        mode = null
    }

    override fun anim(seq: SeqType, delay: Int, priority: Int) {
        val setSequence = PathingEntityCommon.anim(this, seq, delay, priority)
        if (!setSequence) {
            return
        }
        if (pendingSequence == EntitySeq.ZERO) {
            infoProtocol.setSequence(-1, 0)
        } else {
            infoProtocol.setSequence(pendingSequence.id, pendingSequence.delay)
        }
    }

    public fun say(text: String) {
        infoProtocol.setSay(text)
    }

    public fun facePlayer(target: Player) {
        PathingEntityCommon.facePlayer(this, target)
        infoProtocol.setFacePathingEntity(faceEntitySlot)
    }

    public fun faceNpc(target: Npc) {
        PathingEntityCommon.faceNpc(this, target)
        infoProtocol.setFacePathingEntity(faceEntitySlot)
    }

    public fun resetFaceEntity() {
        PathingEntityCommon.resetFaceEntity(this)
        infoProtocol.setFacePathingEntity(faceEntitySlot)
    }

    public fun facingTarget(playerList: PlayerList): Player? =
        if (isFacingPlayer) {
            playerList[faceEntitySlot - PathingEntityCommon.FACE_PLAYER_START_SLOT]
        } else {
            null
        }

    public fun facingTarget(npcList: NpcList): Npc? =
        if (isFacingNpc) {
            npcList[faceEntitySlot]
        } else {
            null
        }

    public fun playerEscape(target: Player) {
        mode = NpcMode.PlayerEscape
        facePlayer(target)
    }

    public fun playerFaceClose(target: Player) {
        mode = NpcMode.PlayerFaceClose
        facePlayer(target)
    }

    public fun playerFace(target: Player) {
        mode = NpcMode.PlayerFace
        facePlayer(target)
    }

    public fun playerFace(target: Player, faceFar: Boolean): Unit =
        if (faceFar) {
            playerFace(target)
        } else {
            playerFaceClose(target)
        }

    public fun matches(content: ContentGroupType): Boolean = type.contentGroup == content.id

    public fun forceDespawn() {
        // TODO: force
    }

    /**
     * Suspends the call site until this [Npc] has gone a cycle without moving. If the npc was not
     * moving when this function was called, the coroutine will not suspend and this function will
     * instantly return.
     *
     * For similar functionality with a [Player], it must be done with "protected access."
     *
     * @see [delay]
     */
    public suspend fun GameCoroutine.arriveDelay() {
        if (!hasMovedThisCycle) {
            return
        }
        delay()
    }

    /**
     * Adds a delay to this [Npc] and suspends the coroutine. Once the npc is no longer delayed, the
     * coroutine will resume.
     *
     * For similar functionality with a [Player], it must be done with "protected access."
     *
     * @throws IllegalArgumentException if [cycles] is not greater than 0.
     */
    public suspend fun GameCoroutine.delay(cycles: Int = 1) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        delay(cycles)
        pause { isNotDelayed }
    }

    override fun toString(): String = "Npc(slot=$slotId, coords=$coords, type=$type)"
}
