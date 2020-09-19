package gg.rsmod.plugins.api

import gg.rsmod.game.event.impl.LoginEvent
import gg.rsmod.game.plugin.Plugin

fun Plugin.onLogin(
    stage: LoginEvent.Stage = LoginEvent.Stage.Normal,
    block: LoginEvent.() -> Unit
) {
    onEvent<LoginEvent>()
        .where { this.stage == stage }
        .then(block)
}
