package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.events.PlayerMovementEvent
import org.rsmod.api.player.output.MapFlag.setMapFlag
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteDestination
import org.rsmod.game.movement.RouteRequest
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.Route
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag

public class PlayerMovementProcessor
@Inject
constructor(
    private val collision: CollisionFlagMap,
    private val routeFactory: RouteFactory,
    private val stepFactory: StepFactory,
    private val eventBus: EventBus,
) {
    public fun process(player: Player) {
        player.routeRequest?.let { consumeRequest(player, it) }
        player.routeRequest = null
        player.processMoveSpeed()
        player.resetTempSpeed()
    }

    public fun consumeRequest(player: Player, request: RouteRequest) {
        player.routeTo(request)
    }

    private fun Player.routeTo(request: RouteRequest) {
        val route = routeFactory.create(avatar, request)
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
            setMapFlag(this, dest.x, dest.z)
        }
    }

    private fun Player.processMoveSpeed() {
        if (routeDestination.isEmpty() || !canProcessMovement) {
            return
        }
        processWalkTrigger()

        val completeCrawlStep = moveSpeed == MoveSpeed.Crawl && !hasMovedPreviousCycle
        val steps = if (completeCrawlStep) 1 else moveSpeed.steps
        move(steps)

        val forceWalk = moveSpeed == MoveSpeed.Run && pendingStepCount == 1
        if (forceWalk) {
            moveSpeed = MoveSpeed.Walk
        }
    }

    private fun Player.move(steps: Int) {
        val destination = routeDestination
        val waypoint = destination.peekFirst() ?: return
        val start = coords
        var current = start
        var target = waypoint
        var stepCount = 0
        lastProcessedCoord = start
        removeBlockWalkCollision(current)
        for (i in 0 until steps) {
            // Important to set this before `current` is assigned for this iteration. This serves
            // as a way to track the intermediate coord when running.
            lastProcessedCoord = current

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
        if (current == target) {
            // If last step in on waypoint destination, remove it from queue.
            destination.pollFirst()
        }
        if (destination.isEmpty()) {
            clearMapFlag()
        }
        addBlockWalkCollision(current)
        updateMovementClock(current, start)
        pendingStepCount = stepCount
        coords = current
    }

    private fun Player.validatedStep(current: CoordGrid, target: CoordGrid): CoordGrid =
        stepFactory.validated(
            source = current,
            dest = target,
            size = size,
            extraFlag = CollisionFlag.BLOCK_PLAYERS,
        )

    private fun Player.addBlockWalkCollision(coords: CoordGrid) {
        if (!hidden) {
            addBlockWalkCollision(collision, coords)
        }
    }

    private fun Player.removeBlockWalkCollision(coords: CoordGrid) {
        if (!hidden) {
            removeBlockWalkCollision(collision, coords)
        }
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

    private fun Player.processWalkTrigger() {
        val trigger = walkTrigger ?: return
        if (isBusy) {
            return
        }
        clearWalkTrigger()
        val event = PlayerMovementEvent.WalkTrigger(this, trigger)
        eventBus.publish(event)
    }

    private companion object {
        private fun RouteDestination.addAll(route: Route) {
            for (coord in route) {
                add(CoordGrid(coord.x, coord.z, coord.level))
            }
        }
    }
}
