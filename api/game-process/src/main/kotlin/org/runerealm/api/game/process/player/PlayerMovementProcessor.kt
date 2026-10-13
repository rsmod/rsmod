package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.player.events.PlayerMovementEvent
import org.runerealm.api.player.output.MapFlag.setMapFlag
import org.runerealm.api.player.output.clearMapFlag
import org.runerealm.api.player.vars.varMoveSpeed
import org.runerealm.api.route.RouteFactory
import org.runerealm.api.route.StepFactory
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.movement.MoveSpeed
import org.runerealm.game.movement.RouteDestination
import org.runerealm.game.movement.RouteRequest
import org.runerealm.map.CoordGrid
import org.runerealm.routefinder.Route
import org.runerealm.routefinder.collision.CollisionFlagMap
import org.runerealm.routefinder.flag.CollisionFlag

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
            // Important to set this before `current` is assigned for this iteration. This serves
            // as a way to track the intermediate coord when running.
            lastProcessedCoord = current
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
