package org.rsmod.plugins.content.containers

import org.rsmod.game.event.impl.ClientRegister
import org.rsmod.game.event.impl.ServerStartup
import org.rsmod.game.model.item.container.ItemContainerKey
import org.rsmod.game.model.item.container.ItemContainerKeyMap
import org.rsmod.game.model.item.container.ItemContainerStackMode
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.mob.player.addContainer

val keys: ItemContainerKeyMap by inject()

val inventoryComponent = component("inventory_container").toComponent()

val inventoryKey = ItemContainerKey.of(
    "inventory",
    ItemContainerStackMode.Default,
    DefaultContainer.INVENTORY_SIZE,
    DefaultContainer.INVENTORY_CLIENT_ID,
    inventoryComponent
)

val equipmentKey = ItemContainerKey.ofClientId(
    "equipment",
    ItemContainerStackMode.Default,
    DefaultContainer.EQUIPMENT_SIZE,
    DefaultContainer.EQUIPMENT_CLIENT_ID
)

val bankKey = ItemContainerKey.ofClientId(
    "bank",
    ItemContainerStackMode.Always,
    DefaultContainer.BANK_SIZE,
    DefaultContainer.BANK_CLIENT_ID
)

onEvent<ClientRegister>()
    .then { client.player.setDefaultContainers() }

onEvent<ServerStartup>()
    .then { registerContainerKeys() }

fun Player.setDefaultContainers() {
    addContainer(inventoryKey, inventory)
    addContainer(equipmentKey, equipment)
    addContainer(bankKey, bank)

    /* enable auto-updates for necessary containers */
    inventory.autoUpdate = true
    equipment.autoUpdate = true
}

fun registerContainerKeys() {
    keys.register(inventoryKey)
    keys.register(equipmentKey)
    keys.register(bankKey)
}
