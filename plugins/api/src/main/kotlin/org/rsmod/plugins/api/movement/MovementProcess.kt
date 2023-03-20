package org.rsmod.plugins.api.movement

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.MobEntity
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.model.mob.list.forEachNotNull
import org.rsmod.game.model.mob.move.MovementSpeed
import org.rsmod.game.model.route.RouteRequest
import org.rsmod.game.model.route.RouteRequestCoordinates
import org.rsmod.game.model.route.RouteRequestEntity
import org.rsmod.game.pathfinder.Route
import org.rsmod.plugins.api.clearMinimapFlag
import org.rsmod.plugins.api.displace
import org.rsmod.plugins.api.model.route.RouteRequestGameObject
import org.rsmod.plugins.api.pathfinder.RouteFactory
import org.rsmod.plugins.api.pathfinder.StepFactory
import org.rsmod.plugins.api.sendTempMovement
import org.rsmod.plugins.api.setMinimapFlag
import javax.inject.Inject
import javax.inject.Singleton

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
        val route = request.createRoute(entity)
        movement.addAll(route)
        if (route.failed) {
            clearMinimapFlag()
        } else if (route.alternative) {
            val dest = route.last()
            setMinimapFlag(dest.x, dest.z)
        }
    }

    private fun Player.movementCycle() {
        if (movement.isEmpty()) return
        var stepsTaken = 0
        var curr = coords
        val steps = movement.speed.steps
        for (i in 0 until steps) {
            if (movement.queue.isEmpty()) pollNextWaypoint(curr)
            curr = movement.queue.poll() ?: break
            stepsTaken++
        }
        applyTempMovement(movement.speed, stepsTaken)
        coords = curr
    }

    private fun Player.pollNextWaypoint(source: Coordinates) {
        val waypoint = movement.waypoints.poll() ?: return
        val path = stepFactory.createPath(source, waypoint)
        movement.queue.addAll(path)
    }

    private fun Player.applyTempMovement(speed: MovementSpeed, stepsTaken: Int) {
        if (speed == MoveSpeed.Run && stepsTaken == MoveSpeed.Walk.steps) {
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

    private val Player?.asyncRouteRequest: Boolean get() = this?.routeRequest?.async == true
}
