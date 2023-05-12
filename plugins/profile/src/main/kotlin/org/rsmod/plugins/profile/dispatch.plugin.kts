package org.rsmod.plugins.profile

import org.rsmod.plugins.api.core.GameProcessEvent
import org.rsmod.plugins.api.onEvent
import org.rsmod.plugins.profile.dispatch.client.ClientDeregisterDispatch
import org.rsmod.plugins.profile.dispatch.client.ClientRegisterDispatch
import org.rsmod.plugins.profile.dispatch.player.PlayerDeregisterDispatch
import org.rsmod.plugins.profile.dispatch.player.PlayerRegisterDispatch

private val playerRegister: PlayerRegisterDispatch by inject()
private val playerDeregister: PlayerDeregisterDispatch by inject()
private val clientRegister: ClientRegisterDispatch by inject()
private val clientDeregister: ClientDeregisterDispatch by inject()

onEvent<GameProcessEvent.EndCycle> {
    playerRegister.serve()
    clientRegister.serve()
    playerDeregister.serve()
    clientDeregister.serve()
}
