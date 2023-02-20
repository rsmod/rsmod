package org.rsmod.plugins.api.info

import org.rsmod.game.events.GameEventBus
import org.rsmod.plugins.api.info.player.SingleThreadedPlayerInfoTask
import org.rsmod.plugins.api.model.event.PlayerSession

private val events: GameEventBus by inject()
private val task: SingleThreadedPlayerInfoTask by inject()

events.subscribe<PlayerSession.Initialize> { task.initialize(player) }
events.subscribe<PlayerSession.LogOut> { task.finalize(player) }
