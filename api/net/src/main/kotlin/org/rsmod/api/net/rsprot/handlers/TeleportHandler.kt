package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.user.Teleport
import org.rsmod.api.config.refs.modlevels
import org.rsmod.api.net.rsprot.player.protectedTelejump
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.realm.Realm
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionFlagMap

class TeleportHandler
@Inject
constructor(
    private val realm: Realm,
    private val eventBus: EventBus,
    private val collision: CollisionFlagMap,
) : MessageHandler<Teleport> {
    override fun handle(player: Player, message: Teleport) {
        if (player.isDelayed) {
            player.clearMapFlag()
            return
        }
        val dest = CoordGrid(message.x, message.z, player.level)
        player.clearPendingAction(eventBus)
        player.resetFaceEntity()
        if (player.modLevel.hasAccessTo(modlevels.admin)) {
            player.protectedTelejump(collision, dest)
        }
    }
}
