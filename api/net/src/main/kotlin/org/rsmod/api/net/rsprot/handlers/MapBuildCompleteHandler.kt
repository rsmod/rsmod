package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.misc.client.MapBuildComplete
import org.rsmod.game.entity.Player

class MapBuildCompleteHandler : MessageHandler<MapBuildComplete> {
    override fun handle(player: Player, message: MapBuildComplete) {
        player.lastMapBuildComplete = player.currentMapClock
    }
}
