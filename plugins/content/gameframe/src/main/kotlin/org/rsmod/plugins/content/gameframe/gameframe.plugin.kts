package org.rsmod.plugins.content.gameframe

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.subscribe
import org.rsmod.plugins.api.model.event.PlayerSession

private val plugin: GameframePlugin by inject()
private val events: GameEventBus by inject()

events.subscribe<PlayerSession.Initialize> { plugin.initialize(player) }
