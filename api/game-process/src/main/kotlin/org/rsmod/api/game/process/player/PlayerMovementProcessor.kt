package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.clearMapFlag
import org.rsmod.api.player.setMapFlag
import org.rsmod.api.player.varMoveSpeed
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteDestination
import org.rsmod.game.movement.RouteRequest
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.Route
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.flag.CollisionFlag

public class PlayerMovementProcessor
@Inject
constructor(
    private val collision: CollisionFlagMap,
    private val routeFactory: RouteFactory,
    private val stepFactory: StepFactory,
) {
    public fun process(player: Player) {
        player.routeRequest?.let { consumeRequest(player, it) }
        player.routeRequest = null
        player.recalcRequests()
        player.processMoveSpeed()
        player.resetTempSpeed()
    }

    public fun consumeRequest(player: Player, request: RouteRequest) {
        player.routeTo(request)
    }

    private fun Player.recalcRequests() {
        val destination = routeDestination
        if (destination.size <= 1 && !isBusy) {
            val recalc = destination.recalcRequest
            recalc?.let {
                destination.clear()
                routeTo(it)
            }
        }
    }

    private fun Player.routeTo(request: RouteRequest) {
        val route = routeFactory.create(avatar, request)
        val recalc = if (request.recalc) request else null
        routeDestination.recalcRequest = recalc
        cachedMoveSpeed = tempMoveSpeed ?: varMoveSpeed
        moveSpeed = cachedMoveSpeed
        consumeRoute(route)
    }

    private fun Player.consumeRoute(route: Route) {
        routeDestination.clear()
        routeDestination.addAll(route)
        if (route.isEmpty()) {
            clearMapFlag()
        } else {
            val dest = route.last()
            setMapFlag(dest.x, dest.z)
        }
        if (route.failed) {
            emulateLogInWalkTo()
        }
    }

    private fun Player.processMoveSpeed() {
        if (!moveSpeed.processRouteDestination) {
            return
        }
        if (routeDestination.isEmpty()) {
            moveSpeed = MoveSpeed.Stationary
            return
        }
        val steps = move(moveSpeed.steps)
        if (steps > 0) {
            moveSpeed = speedOffset(moveSpeed, steps)
        }
    }

    private fun Player.move(steps: Int): Int {
        val destination = routeDestination
        val waypoint = destination.peekFirst() ?: return 0
        val start = coords
        var current = start
        var target = waypoint
        var stepCount = 0
        removeBlockWalkCollision(current)
        for (i in 0 until steps) {
            if (current == target) {
                target = destination.pollFirst() ?: break
                if (current == target) {
                    target = destination.peekFirst() ?: break
                }
            }
            val step = validatedStep(current, target)
            if (step == CoordGrid.NULL) {
                break
            }
            current = step
            stepCount++
        }
        addBlockWalkCollision(current)
        updateMovementClock(current, start)
        // If last step in on waypoint destination, remove it from queue.
        if (current == target) {
            destination.pollFirst()
        }
        if (destination.isEmpty()) {
            clearMapFlag()
        }
        coords = current
        currentWaypoint = target
        return stepCount
    }

    private fun Player.validatedStep(current: CoordGrid, target: CoordGrid): CoordGrid =
        stepFactory.validated(
            source = current,
            dest = target,
            size = size,
            extraFlag = CollisionFlag.BLOCK_PLAYERS,
        )

    private fun Player.addBlockWalkCollision(coords: CoordGrid) {
        addBlockWalkCollision(collision, coords)
    }

    private fun Player.removeBlockWalkCollision(coords: CoordGrid) {
        removeBlockWalkCollision(collision, coords)
    }

    private fun Player.resetTempSpeed() {
        if (routeDestination.isEmpty()) {
            tempMoveSpeed = null
        }
    }

    private fun Player.updateMovementClock(current: CoordGrid, previous: CoordGrid) {
        if (current != previous) {
            lastMovement = currentMapClock
        }
    }

    /**
     * This emulates an edge-case mechanic (bug).
     *
     * The bug occurs on the first player route request after log-in, as long as they have _not_
     * moved at all (or teleported). If the route request _fails_, the player will begin _walking_
     * towards coordinates [0,0]; only stopping if the path is blocked along the way.
     *
     * @see [Route.failed]
     */
    private fun Player.emulateLogInWalkTo() {
        if (currentWaypoint != CoordGrid.ZERO) return
        moveSpeed = MoveSpeed.Walk
        routeDestination.add(CoordGrid.ZERO)
    }

    private companion object {
        private fun RouteDestination.addAll(route: Route) {
            this += route.map { CoordGrid(it.x, it.z, it.level) }
        }

        private fun speedOffset(previous: MoveSpeed, steps: Int): MoveSpeed =
            when {
                steps == 0 && previous == MoveSpeed.Crawl -> MoveSpeed.Crawl
                steps == 1 -> MoveSpeed.Walk
                steps == 2 -> MoveSpeed.Run
                else -> MoveSpeed.Stationary
            }
    }
}
