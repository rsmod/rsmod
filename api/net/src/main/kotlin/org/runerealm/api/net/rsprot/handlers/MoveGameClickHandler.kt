package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.user.MoveGameClick
import org.runerealm.api.net.rsprot.player.modLevelTeleMoveSpeed
import org.runerealm.api.net.rsprot.player.protectedTelejump
import org.runerealm.api.player.output.clearMapFlag
import org.runerealm.api.player.protect.clearPendingAction
import org.runerealm.api.player.vars.ctrlMoveSpeed
import org.runerealm.api.realm.Realm
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.movement.MoveSpeed
import org.runerealm.game.movement.RouteRequestCoord
import org.runerealm.map.CoordGrid
import org.runerealm.routefinder.collision.CollisionFlagMap

class MoveGameClickHandler
@Inject
constructor(
    private val realm: Realm,
    private val eventBus: EventBus,
    private val collision: CollisionFlagMap,
) : MessageHandler<MoveGameClick> {
    override fun handle(player: Player, message: MoveGameClick) {
        if (player.isDelayed) {
            player.clearMapFlag()
            return
        }
        val dest = CoordGrid(message.x, message.z, player.level)
        val speed =
            when (message.keyCombination) {
                1 -> player.ctrlMoveSpeed()
                2 -> player.modLevelTeleMoveSpeed(developmentMode = realm.config.devMode)
                else -> null
            }
        player.clearPendingAction(eventBus)
        player.resetFaceEntity()
        if (speed == MoveSpeed.Stationary) {
            player.protectedTelejump(collision, dest)
        } else {
            val request = RouteRequestCoord(dest, clientRequest = true)
            player.routeRequest = request
            player.tempMoveSpeed = speed
        }
    }
}
