package org.rsmod.plugins.api.update.player.task

import org.rsmod.game.coroutine.delay
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.PlayerList
import org.rsmod.game.model.move.MoveRequest
import org.rsmod.game.update.task.UpdateTask
import org.rsmod.plugins.api.model.mob.player.clearMinimapFlag
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.model.mob.player.sendMinimapFlag
import javax.inject.Inject

class PathFinderTask @Inject constructor(private val playerList: PlayerList) : UpdateTask {

    override suspend fun execute() {
        playerList.forEach { player ->
            val request = player?.moveRequest ?: return@forEach
            player.handleMoveRequest(request)
        }
    }

    private fun Player.handleMoveRequest(request: MoveRequest) {
        if (request.stopPreviousMovement) {
            stopMovement()
        }
        clearQueues()
        val route = request.buildRoute()
        if (route.isEmpty()) {
            clearMinimapFlag()
            if (route.failed) {
                request.cannotReachMessage?.let { sendMessage(it) }
            } else if (!route.alternative) {
                request.reachAction()
            }
            return
        }
        movement.speed = request.tempSpeed
        movement.addAll(route)
        val dest = route.last()
        sendMinimapFlag(dest)
        normalQueue {
            delay()
            var reached = false
            while (!reached) {
                if (coords == dest) {
                    reached = true
                    break
                }
                delay()
            }
            if (!reached || route.alternative) {
                request.cannotReachMessage?.let { sendMessage(it) }
                return@normalQueue
            }
            request.reachAction()
        }
    }
}
