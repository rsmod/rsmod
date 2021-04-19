package org.rsmod.plugins.content.containers

import org.rsmod.game.event.impl.ClientRegister
import org.rsmod.game.model.item.container.ItemContainerKeyMap

val keys: ItemContainerKeyMap by inject()

keys.register(inventoryKey)
keys.register(equipmentKey)
keys.register(bankKey)

onEvent<ClientRegister>()
    .then { client.player.setDefaultContainers() }
