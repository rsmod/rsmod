package org.rsmod.plugins.api.container

import org.rsmod.game.event.impl.ClientRegister
import org.rsmod.game.event.impl.ItemContainerInitialize
import org.rsmod.game.event.impl.ItemContainerUpdate

onEvent<ClientRegister>()
    .then { client.player.setDefaultContainers() }

onEvent<ItemContainerInitialize>()
    .then { player.initializeDefaultContainer(key, container) }

onEvent<ItemContainerUpdate>()
    .then { player.updateDefaultContainer(key, container) }
