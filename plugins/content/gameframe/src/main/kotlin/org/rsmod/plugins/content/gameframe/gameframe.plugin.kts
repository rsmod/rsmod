package org.rsmod.plugins.content.gameframe

import org.rsmod.game.events.GameEventBus
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.openGameframe

public val events: GameEventBus by inject()

events.subscribe<PlayerSession.Initialize> {
    player.openGameframe(GameframeResizeNormal)
}
