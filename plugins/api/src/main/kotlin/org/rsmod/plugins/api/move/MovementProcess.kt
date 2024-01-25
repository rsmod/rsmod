package org.rsmod.plugins.api.move

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.MobEntity
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.model.mob.list.forEachNotNull
import org.rsmod.game.model.mob.move.MovementQueue
import org.rsmod.game.model.mob.move.MovementSpeed
import org.rsmod.game.model.route.RouteRequest
import org.rsmod.game.model.route.RouteRequestCoordinates
import org.rsmod.game.model.route.RouteRequestEntity
import org.rsmod.game.pathfinder.Route
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.plugins.api.clearMinimapFlag
import org.rsmod.plugins.api.displace
import org.rsmod.plugins.api.model.route.RouteRequestGameObject
import org.rsmod.plugins.api.pathfinder.RouteFactory
import org.rsmod.plugins.api.pathfinder.StepFactory
import org.rsmod.plugins.api.sendTempMovement
import org.rsmod.plugins.api.setMinimapFlag
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
public class MovementProcess @Inject constructor(
    private val players: PlayerList,
    private val routeFactory: RouteFactory,
    private val stepFactory: StepFactory
) {

    public fun execute(): Unit = runBlocking {
        coroutineScope {
            players.forEachNotNull {
                if (!it.asyncRouteRequest) return@forEachNotNull
                appendAsyncRouteRequest(it)
            }
        }
        players.forEachNotNull {
            it.appendRouteRequest(async = false)
            it.movementCycle()
        }
    }

    private fun Player.appendRouteRequest(async: Boolean) {
        val request = routeRequest ?: return
        if (request.async != async) return
        // TODO: check player can move, etc
        movement.clear()
        appendRouteRequest(request)
        routeRequest = null
    }

    private fun Player.appendRouteRequest(request: RouteRequest) {
        if (request.speed == MoveSpeed.Displace) {
            val destination = request.displaceDestination(entity)
            displace(destination)
            return
        }
        // TODO: assign "temporary" move speed for this specific route
        // based on [RouteRequest.speed]
        val route = request.createRoute(entity)
        movement.addAll(route)
        if (route.failed) {
            clearMinimapFlag()
            emulateLogInWalkTo()
        } else if (route.alternative) {
            val dest = route.last()
            setMinimapFlag(dest.x, dest.z)
        }
    }

    private fun Player.movementCycle(speed: MovementSpeed = movement.speed) {
        var waypoint = movement.peek() ?: return
        var curr = coords
        var stepCount = 0
        for (i in 0 until speed.steps) {
            if (curr == waypoint) {
                movement.remove()
                waypoint = movement.peek() ?: break
            }
            val step = stepFactory.validated(curr, waypoint, extraFlag = EXTRA_CLIP_VALIDATION)
            if (step == Coordinates.NULL) break
            stepCount++
            curr = step
        }
        // If last step in on waypoint destination, remove it from queue.
        if (curr == waypoint) movement.poll()
        applyTempMovement(speed, stepCount)
        movement.lastStep = curr
        coords = curr
    }

    private fun Player.applyTempMovement(speed: MovementSpeed, stepCount: Int) {
        if (speed == MoveSpeed.Run && stepCount == MoveSpeed.Walk.steps) {
            sendTempMovement(MoveSpeed.Walk)
        }
    }

    private fun RouteRequest.createRoute(source: MobEntity): Route = when (this) {
        is RouteRequestCoordinates -> routeFactory.create(source, destination, async = async)
        is RouteRequestEntity -> routeFactory.create(source, destination, async = async)
        is RouteRequestGameObject -> routeFactory.create(source, destination, async = async)
        else -> error("Unhandled route request type: $this.")
    }

    // TODO: stepFactory.create(Entity) function
    // TODO: stepFactory.create(GameObject) function
    @Suppress("UNUSED_PARAMETER")
    private fun RouteRequest.displaceDestination(source: MobEntity): Coordinates = when (this) {
        is RouteRequestCoordinates -> destination
        is RouteRequestEntity -> destination.coords
        is RouteRequestGameObject -> destination.coords
        else -> error("Unhandled route request type: $this.")
    }

    private fun CoroutineScope.appendAsyncRouteRequest(player: Player) = launch {
        player.appendRouteRequest(async = true)
    }

    /**
     * This emulates an edge-case mechanic (bug).
     *
     * The bug occurs on the first player route request after log-in, as long as
     * they have _not_ moved at all (or teleported). If the route request _fails_,
     * the player will begin _walking_ towards coordinates [0,0]; only stopping
     * if the path is blocked along the way.
     *
     * @see [Route.failed]
     */
    private fun Player.emulateLogInWalkTo() {
        if (movement.lastStep != Coordinates.ZERO) return
        movement.add(movement.lastStep) // Emulate walk-to Coords[0,0] "mechanic"
    }

    private val Player?.asyncRouteRequest: Boolean get() = this?.routeRequest?.async == true

    private companion object {

        private const val EXTRA_CLIP_VALIDATION: Int = CollisionFlag.BLOCK_PLAYERS

        private fun MovementQueue.addAll(route: Route) {
            this += route.map { Coordinates(it.x, it.z, it.level) }
        }
    }
}
