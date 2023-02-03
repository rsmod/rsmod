package org.rsmod.plugins.api.session

import org.rsmod.game.events.EventBus
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.event.ClientSession

private val events: EventBus by inject()
private val players: PlayerList by inject()
private val xtea: XteaRepository by inject()

events.subscribe<ClientSession.Connect> {
    GameSession.connect(client.channel, client.player, players, xtea)
}
