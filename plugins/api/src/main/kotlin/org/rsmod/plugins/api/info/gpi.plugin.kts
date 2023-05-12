package org.rsmod.plugins.api.info

import org.rsmod.plugins.api.info.player.PlayerInfoTask
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.onEvent

private val task: PlayerInfoTask by inject()

onEvent<PlayerSession.Initialize> { task.initialize(this) }
onEvent<PlayerSession.LogOut> { task.finalize(this) }
