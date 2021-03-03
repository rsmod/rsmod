package org.rsmod.plugins.content.skills

import org.rsmod.game.event.impl.ClientRegister
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.onLogin

onEvent<ClientRegister>()
    .then { client.player.setDefaultStats() }

onLogin { StatPlugin.refreshStats(player) }

fun Player.setDefaultStats() {
    if (stats.isNotEmpty()) {
        return
    }
    StatPlugin.setDefaultStats(stats)
}
