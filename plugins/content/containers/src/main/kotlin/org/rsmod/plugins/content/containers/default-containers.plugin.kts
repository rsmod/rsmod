package org.rsmod.plugins.content.containers

import org.rsmod.game.event.impl.ClientRegister
import org.rsmod.game.event.impl.ItemContainerInitialize
import org.rsmod.game.event.impl.ItemContainerUpdate
import org.rsmod.plugins.api.onEarlyLogin

val loginContainers = arrayOf(inventoryKey, equipmentKey)

onEarlyLogin {
    loginContainers.forEach { key ->
        val container = player.containers.getValue(key)
        container.initialized = true
        player.updateFullItemContainer(key, container)
    }
}

onEvent<ClientRegister>()
    .then { client.player.setDefaultContainers() }

onEvent<ItemContainerInitialize>()
    .then { player.updateFullItemContainer(key, container) }

onEvent<ItemContainerUpdate>()
    .then { player.updateDefaultContainer(key, container) }
