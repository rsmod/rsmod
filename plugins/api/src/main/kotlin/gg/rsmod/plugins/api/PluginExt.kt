package gg.rsmod.plugins.api

import gg.rsmod.game.event.impl.LoginEvent
import gg.rsmod.game.plugin.Plugin

fun Plugin.onEarlyLogin(
    block: LoginEvent.() -> Unit
) {
    onEvent<LoginEvent>()
        .where { priority == LoginEvent.Priority.High }
        .then(block)
}

fun Plugin.onLogin(
    block: LoginEvent.() -> Unit
) {
    onEvent<LoginEvent>()
        .where { priority == LoginEvent.Priority.Normal }
        .then(block)
}

fun Plugin.onPostLogin(
    block: LoginEvent.() -> Unit
) {
    onEvent<LoginEvent>()
        .where { priority == LoginEvent.Priority.Low }
        .then(block)
}
