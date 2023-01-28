package org.rsmod.plugins.api

import org.rsmod.game.events.EventBus
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.event.ClientSession
import org.rsmod.plugins.api.session.GameSession

val events: EventBus by inject()
val players: PlayerList by inject()
val xtea: XteaRepository by inject()

events.subscribe<ClientSession.Connect> {
    GameSession.connect(this, players, xtea)
}
