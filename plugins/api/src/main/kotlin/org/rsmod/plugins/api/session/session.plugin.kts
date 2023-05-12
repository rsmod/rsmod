package org.rsmod.plugins.api.session

import org.rsmod.plugins.api.model.event.ClientSession
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.onEvent

private val clientSession: ClientGameSession by inject()
private val playerSession: PlayerGameSession by inject()

onEvent<ClientSession.Connect> { clientSession.connect(client) }
onEvent<ClientSession.Disconnect> { clientSession.disconnect(client) }

onEvent<PlayerSession.LogIn> { playerSession.logIn(this) }
onEvent<PlayerSession.LogOut> { playerSession.logOut(this) }
