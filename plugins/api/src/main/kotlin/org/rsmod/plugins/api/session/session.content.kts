package org.rsmod.plugins.api.session

import org.rsmod.game.events.EventBus
import org.rsmod.plugins.api.model.event.ClientSession

private val events: EventBus by inject()
private val session: GameSession by inject()

events.subscribe<ClientSession.Connect> { session.connect(client.channel, client.player) }
events.subscribe<ClientSession.Disconnect> { session.disconnect(client.channel, client.player) }
