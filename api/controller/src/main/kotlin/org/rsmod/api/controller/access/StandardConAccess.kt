package org.rsmod.api.controller.access

import kotlin.getValue
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.game.entity.Controller
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.map.CoordGrid

/**
 * Manages scoped actions for controllers that implicitly launch a coroutine, allowing functions
 * such as `delay`.
 *
 * This system is extremely similar to `StandardNpcAccess`, however this it has no implicit
 * dependencies and is designed to be as lightweight as possible.
 */
public class StandardConAccess(
    public val controller: Controller,
    private val coroutine: GameCoroutine,
) {
    public val coords: CoordGrid by controller::coords
    public val mapClock: Int by controller::currentMapClock

    public suspend fun delay(cycles: Int = 1) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        controller.delay(cycles)
        coroutine.pause { controller.isNotDelayed }
    }

    public fun duration(cycles: Int) {
        controller.duration(cycles)
    }

    public fun resetDuration() {
        controller.resetDuration()
    }

    public fun aiTimer(cycles: Int) {
        controller.aiTimer(cycles)
    }

    public fun timer(timerType: TimerType, cycles: Int) {
        controller.timer(timerType, cycles)
    }

    public fun aiQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        controller.aiQueue(queue, cycles, args)
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        controller.queue(queue, cycles, args)
    }

    override fun toString(): String =
        "StandardConAccess(controller=$controller, coroutine=$coroutine)"
}
