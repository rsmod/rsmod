package org.runerealm.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.misc.client.MapBuildComplete
import org.runerealm.game.entity.Player

class MapBuildCompleteHandler : MessageHandler<MapBuildComplete> {
    override fun handle(player: Player, message: MapBuildComplete) {
        player.lastMapBuildComplete = player.currentMapClock
    }
}
