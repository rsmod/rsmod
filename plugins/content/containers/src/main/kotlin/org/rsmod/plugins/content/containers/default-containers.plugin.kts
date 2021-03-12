package org.rsmod.plugins.content.containers

import org.rsmod.game.event.impl.ClientRegister

onEvent<ClientRegister>()
    .then { client.player.setDefaultContainers() }
