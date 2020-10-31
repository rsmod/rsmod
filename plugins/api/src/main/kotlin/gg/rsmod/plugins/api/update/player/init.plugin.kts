package gg.rsmod.plugins.api.update.player

import gg.rsmod.game.event.impl.ClientRegister
import gg.rsmod.plugins.api.update.player.task.PlayerUpdateTask

val updateTask: PlayerUpdateTask by inject()

onEvent<ClientRegister>().then(::onRegister)

fun onRegister(event: ClientRegister) {
    updateTask.initClient(event.client)
}
