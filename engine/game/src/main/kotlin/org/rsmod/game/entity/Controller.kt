package org.rsmod.game.entity

import kotlin.coroutines.startCoroutine
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.coroutine.suspension.GameCoroutineSimpleCompletion
import org.rsmod.game.queue.NpcQueueList
import org.rsmod.game.timer.NpcTimerMap
import org.rsmod.game.type.controller.ControllerType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.vars.VarConIntMap
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds

public class Controller(public val type: ControllerType, public val coords: CoordGrid) {
    public val id: Int = type.id
    public val timerMap: NpcTimerMap = NpcTimerMap()
    public val queueList: NpcQueueList = NpcQueueList()
    public val vars: VarConIntMap by lazy { VarConIntMap() }

    public var slotId: Int = INVALID_SLOT
    public var durationStart: Int = -1
    public var duration: Int = -1
    public var delay: Int = -1
    public var creationCycle: Int = -1

    public var currentMapClock: Int = -1
    public var aiTimerStart: Int = -1
    public var aiTimer: Int = -1
    public var aiQueue: NpcQueueList.Queue? = null
    public var activeCoroutine: GameCoroutine? = null

    public val isDelayed: Boolean
        get() = delay > currentMapClock

    public val isNotDelayed: Boolean
        get() = !isDelayed

    public fun delay(cycles: Int = 1) {
        this.delay = currentMapClock + cycles
    }

    public fun duration(cycles: Int) {
        this.durationStart = cycles
        this.duration = cycles
    }

    public fun resetDuration() {
        duration = durationStart
    }

    public fun aiTimer(cycles: Int) {
        this.aiTimerStart = cycles
        this.aiTimer = cycles
    }

    public fun timer(timer: TimerType, cycles: Int) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        timerMap.schedule(timer, interval = cycles)
    }

    public fun clearTimer(timer: TimerType) {
        timerMap.remove(timer)
    }

    public fun aiQueue(type: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        val queue = NpcQueueList.Queue(type.id, cycles, args)
        this.aiQueue = queue
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        queueList.add(queue, cycles, args)
    }

    public fun launch(
        coroutine: GameCoroutine = GameCoroutine(),
        block: suspend GameCoroutine.() -> Unit,
    ): GameCoroutine {
        cancelActiveCoroutine()
        val completion = GameCoroutineSimpleCompletion
        block.startCoroutine(coroutine, completion)
        if (coroutine.isSuspended) {
            activeCoroutine = coroutine
        }
        return coroutine
    }

    public fun advanceActiveCoroutine() {
        activeCoroutine?.advance()
        if (activeCoroutine?.isIdle == true) {
            activeCoroutine = null
        }
    }

    public fun resumeActiveCoroutine(withValue: Any) {
        activeCoroutine?.resumeWith(withValue)
        if (activeCoroutine?.isIdle == true) {
            activeCoroutine = null
        }
    }

    public fun cancelActiveCoroutine() {
        activeCoroutine?.cancel()
        activeCoroutine = null
    }

    public fun bounds(): Bounds = Bounds(coords)

    /**
     * @throws IllegalArgumentException if [target]'s [Bounds.level] is not equal to this
     *   [CoordGrid.level].
     */
    public fun distanceTo(target: Bounds): Int = bounds().distanceTo(target)

    /**
     * @return true if the [target] is within [distance] tiles from [coords] _and_ the [target]'s
     *   [Bounds.level] is equal to this [CoordGrid.level].
     *
     * This takes into account the width and length dimensions of [target].
     */
    public fun isWithinDistance(target: Bounds, distance: Int): Boolean =
        coords.level == target.level && bounds().isWithinDistance(target, distance)

    /**
     * This method is responsible for cleaning up any ongoing tasks that the controller may be
     * responsible for. This includes things such as coroutines; ex: [activeCoroutine]. If these
     * coroutines are not cancelled properly they may linger in memory and run indefinitely.
     */
    public fun destroy() {
        cancelActiveCoroutine()
    }

    override fun toString(): String =
        "Controller(" +
            "slot=$slotId, " +
            "coords=$coords, " +
            "creationCycle=$creationCycle, " +
            "startDuration=$durationStart, " +
            "duration=$duration, " +
            "mapClock=$currentMapClock, " +
            "type=$type, " +
            "aiTimer=$aiTimer, " +
            "aiQueue=$aiQueue, " +
            "activeCoroutine=$activeCoroutine, " +
            "vars=$vars, " +
            "timers=$timerMap, " +
            "queues=$queueList" +
            ")"

    public companion object {
        public const val INVALID_SLOT: Int = -1
    }
}
