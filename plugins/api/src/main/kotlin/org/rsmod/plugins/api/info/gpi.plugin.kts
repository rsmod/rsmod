package org.rsmod.plugins.api.info

import org.rsmod.plugins.api.info.player.PlayerInfoTask
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.subscribe

private val task: PlayerInfoTask by inject()

subscribe<PlayerSession.Initialize> { task.initialize(this) }
subscribe<PlayerSession.LogOut> { task.finalize(this) }
