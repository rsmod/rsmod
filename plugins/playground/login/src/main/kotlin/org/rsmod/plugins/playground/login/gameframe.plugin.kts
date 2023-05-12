package org.rsmod.plugins.playground.login

import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.openGameframe
import org.rsmod.plugins.api.onEvent
import org.rsmod.plugins.content.gameframe.GameframePlugin

private val gameframe: GameframePlugin by inject()

onEvent<PlayerSession.Initialize> {
    openGameframe(gameframe.fixed)
}
