package org.rsmod.plugins.api.info

import org.rsmod.game.events.EventBus
import org.rsmod.plugins.api.model.event.PlayerSession

private val events: EventBus by inject()
private val task: GPITask by inject()

events.subscribe<PlayerSession.Initialize> { task.initialize(player) }
