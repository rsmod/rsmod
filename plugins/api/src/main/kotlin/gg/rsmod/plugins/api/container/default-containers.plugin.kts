package gg.rsmod.plugins.api.container

import gg.rsmod.game.event.impl.ClientRegister
import gg.rsmod.game.event.impl.ItemContainerUpdate

onEvent<ClientRegister>()
    .then { client.player.setContainers() }

onEvent<ItemContainerUpdate>()
    .then { player.updateDefaultContainer(key, container) }
