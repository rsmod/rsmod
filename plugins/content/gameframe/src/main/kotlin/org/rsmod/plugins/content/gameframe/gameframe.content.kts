package org.rsmod.plugins.content.gameframe

import org.rsmod.game.events.EventBus
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.openGameframe

public val events: EventBus by inject()

events.subscribe<PlayerSession.Initialize> {
    player.openGameframe(GameframeResizeNormal)
}
