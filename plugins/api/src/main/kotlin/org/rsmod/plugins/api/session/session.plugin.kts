package org.rsmod.plugins.api.session

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.subscribe
import org.rsmod.plugins.api.model.event.ClientSession
import org.rsmod.plugins.api.model.event.PlayerSession

private val events: GameEventBus by inject()
private val clientSession: ClientGameSession by inject()
private val playerSession: PlayerGameSession by inject()

events.subscribe<ClientSession.Connect> { clientSession.connect(client) }
events.subscribe<ClientSession.Disconnect> { clientSession.disconnect(client) }

events.subscribe<PlayerSession.LogIn> { playerSession.logIn(player) }
events.subscribe<PlayerSession.LogOut> { playerSession.logOut(player) }
