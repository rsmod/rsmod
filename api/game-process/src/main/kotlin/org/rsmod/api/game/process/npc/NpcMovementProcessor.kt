package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.route.StepFactory
import org.rsmod.game.entity.Npc
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteRequest
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.game.movement.RouteRequestLoc
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.RouteCoordinates
import org.rsmod.pathfinder.RouteFinding
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.collision.CollisionStrategy

public class NpcMovementProcessor
@Inject
constructor(private val collision: CollisionFlagMap, private val stepFactory: StepFactory) {
    public fun process(npc: Npc) {
        npc.routeRequest?.let { consumeRequest(npc, it) }
        npc.routeRequest = null
        npc.processMovement()
        npc.updateRspAvatar()
        npc.resetTempSpeed()
    }

    public fun consumeRequest(npc: Npc, request: RouteRequest) {
        npc.routeTo(request)
    }

    private fun Npc.routeTo(request: RouteRequest) {
        val dest = request.destination(this)
        cachedMoveSpeed = tempMoveSpeed ?: defaultMoveSpeed
        moveSpeed = cachedMoveSpeed
        routeDestination.clear()
        routeDestination.add(dest)
    }

    private fun Npc.processMovement() {
        val collision = collisionStrategy
        if (collision != null) {
            processMoveSpeed(collision)
        } else if (routeDestination.isNotEmpty()) {
            resetMovement()
        }
    }

    private fun Npc.processMoveSpeed(collision: CollisionStrategy) {
        if (!moveSpeed.processRouteDestination) {
            return
        }
        if (routeDestination.isEmpty()) {
            moveSpeed = MoveSpeed.Stationary
            return
        }
        val steps = move(moveSpeed.steps, collision)
        if (steps > 0) {
            moveSpeed = speedOffset(moveSpeed, steps)
        }
    }

    private fun Npc.move(steps: Int, collision: CollisionStrategy): Int {
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
            val step = stepFactory.validated(this, current, target, collision)
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
        coords = current
        currentWaypoint = target
        return stepCount
    }

    private fun Npc.resetMovement() {
        moveSpeed = MoveSpeed.Stationary
        abortRoute()
        clearInteraction()
    }

    private fun Npc.addBlockWalkCollision(coords: CoordGrid) {
        addBlockWalkCollision(collision, coords)
    }

    private fun Npc.removeBlockWalkCollision(coords: CoordGrid) {
        removeBlockWalkCollision(collision, coords)
    }

    // TODO: Use actual movement direction opcodes instead of distance to avoid sync issues with
    //  client route finder.
    private fun Npc.updateRspAvatar() {
        if (previousCoords == coords) {
            return
        }
        val prevCoords = previousCoords
        val deltaX = coords.x - prevCoords.x
        val deltaZ = coords.z - prevCoords.z
        val distance = coords.chebyshevDistance(prevCoords)
        when (distance) {
            0 -> return
            1 -> infoProtocol.walk(deltaX, deltaZ)
            else -> infoProtocol.teleport(coords.x, coords.z, coords.level, jump = false)
        }
    }

    private fun Npc.resetTempSpeed() {
        if (routeDestination.isEmpty()) {
            tempMoveSpeed = null
        }
    }

    private fun Npc.updateMovementClock(current: CoordGrid, previous: CoordGrid) {
        if (current != previous) {
            lastMovement = currentMapClock
        }
    }

    private fun RouteRequest.destination(source: Npc): CoordGrid =
        when (this) {
            is RouteRequestCoord -> destination
            is RouteRequestPathingEntity ->
                RouteFinding.naiveDestination(
                        sourceX = source.coords.x,
                        sourceZ = source.coords.z,
                        sourceWidth = source.size,
                        sourceLength = source.size,
                        targetX = destination.coords.x,
                        targetZ = destination.coords.z,
                        targetWidth = destination.size,
                        targetLength = destination.size,
                    )
                    .toCoordGrid()
            is RouteRequestLoc -> destination
        }

    private companion object {
        private fun speedOffset(previous: MoveSpeed, steps: Int): MoveSpeed =
            when {
                steps == 0 && previous == MoveSpeed.Crawl -> MoveSpeed.Crawl
                steps == 1 -> MoveSpeed.Walk
                steps == 2 -> MoveSpeed.Run
                else -> MoveSpeed.Stationary
            }

        private fun RouteCoordinates.toCoordGrid() = CoordGrid(x, z, level)
    }
}
