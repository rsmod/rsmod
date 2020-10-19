package gg.rsmod.plugins.core.update.player

import gg.rsmod.game.event.impl.ClientRegister

val updateTask: PlayerUpdateTask by inject()

onEvent<ClientRegister>().then(::onRegister)

fun onRegister(event: ClientRegister) {
    updateTask.initClient(event.client)
}
