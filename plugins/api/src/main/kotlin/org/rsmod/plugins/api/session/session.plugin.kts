package org.rsmod.plugins.api.session

import org.rsmod.plugins.api.model.event.ClientSession
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.subscribe

private val clientSession: ClientGameSession by inject()
private val playerSession: PlayerGameSession by inject()

subscribe<ClientSession.Connect> { clientSession.connect(client) }
subscribe<ClientSession.Disconnect> { clientSession.disconnect(client) }

subscribe<PlayerSession.LogIn> { playerSession.logIn(this) }
subscribe<PlayerSession.LogOut> { playerSession.logOut(this) }
