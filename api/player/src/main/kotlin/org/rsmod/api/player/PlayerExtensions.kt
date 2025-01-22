package org.rsmod.api.player

import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.output.mes
import org.rsmod.game.entity.Player

public fun Player.forceDisconnect() {
    // TODO: disconnect player
    mes("TODO: Get Disconnected!")
}

public fun Player.clearInteractionRoute() {
    clearInteraction()
    abortRoute()
    clearMapFlag()
}
