package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.user.MoveMinimapClick
import org.rsmod.api.net.rsprot.player.modLevelTeleMoveSpeed
import org.rsmod.api.net.rsprot.player.protectedTelejump
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.api.server.config.WorldConfig
import org.rsmod.api.server.config.isDevRealm
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionFlagMap

class MoveMinimapClickHandler
@Inject
constructor(
    private val config: WorldConfig,
    private val eventBus: EventBus,
    private val collision: CollisionFlagMap,
) : MessageHandler<MoveMinimapClick> {
    override fun handle(player: Player, message: MoveMinimapClick) {
        if (player.isDelayed) {
            player.clearMapFlag()
            return
        }
        val dest = CoordGrid(message.x, message.z, player.level)
        val speed =
            when (message.keyCombination) {
                1 -> player.ctrlMoveSpeed()
                2 -> player.modLevelTeleMoveSpeed(developmentMode = config.isDevRealm)
                else -> null
            }
        player.clearPendingAction(eventBus)
        player.resetFaceEntity()
        if (speed == MoveSpeed.Stationary) {
            player.protectedTelejump(collision, dest)
        } else {
            val request = RouteRequestCoord(dest)
            player.routeRequest = request
            player.tempMoveSpeed = speed
        }
    }
}
