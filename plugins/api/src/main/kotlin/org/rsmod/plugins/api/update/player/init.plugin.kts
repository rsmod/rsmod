package org.rsmod.plugins.api.update.player

import org.rsmod.game.event.impl.ClientRegister
import org.rsmod.plugins.api.update.player.task.PlayerUpdateTask

val updateTask: PlayerUpdateTask by inject()

onEvent<ClientRegister>().then(::onRegister)

fun onRegister(event: ClientRegister) {
    updateTask.initClient(event.client)
}
