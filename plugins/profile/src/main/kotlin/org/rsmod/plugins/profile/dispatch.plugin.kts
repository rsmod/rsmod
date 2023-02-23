package org.rsmod.plugins.profile

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.subscribe
import org.rsmod.game.model.GameProcess
import org.rsmod.plugins.profile.dispatch.client.ClientDeregisterDispatch
import org.rsmod.plugins.profile.dispatch.client.ClientRegisterDispatch
import org.rsmod.plugins.profile.dispatch.player.PlayerDeregisterDispatch
import org.rsmod.plugins.profile.dispatch.player.PlayerRegisterDispatch

private val events: GameEventBus by inject()
private val playerRegister: PlayerRegisterDispatch by inject()
private val playerDeregister: PlayerDeregisterDispatch by inject()
private val clientRegister: ClientRegisterDispatch by inject()
private val clientDeregister: ClientDeregisterDispatch by inject()

events.subscribe<GameProcess.EndCycle> {
    playerRegister.serve()
    clientRegister.serve()
    playerDeregister.serve()
    clientDeregister.serve()
}
