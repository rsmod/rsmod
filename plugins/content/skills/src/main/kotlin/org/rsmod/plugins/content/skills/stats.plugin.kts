package org.rsmod.plugins.content.skills

import org.rsmod.game.event.impl.ClientRegister
import org.rsmod.game.model.mob.Player

onEvent<ClientRegister>()
    .then { client.player.setDefaultStats() }

fun Player.setDefaultStats() {
    StatPlugin.setDefaultStats(stats)
}
