package org.rsmod.plugins.content.containers

import org.rsmod.game.model.item.container.ItemContainerKey
import org.rsmod.game.model.item.container.ItemContainerStackMode
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.model.mob.player.setContainer

private const val INVENTORY_SIZE = 28
private const val EQUIPMENT_SIZE = 12
private const val BANK_SIZE = 800

private const val INVENTORY_ID = 93
private const val EQUIPMENT_ID = 94
private const val BANK_ID = 95

private val inventoryComponent = Component(149, 0)

/*
 * These "default containers" are cached for each player
 * and do not need to be exposed publicly.
 */
internal val inventoryKey = ItemContainerKey.of(
    "inventory",
    ItemContainerStackMode.Default,
    INVENTORY_SIZE,
    INVENTORY_ID,
    inventoryComponent
)

internal val equipmentKey = ItemContainerKey.ofClientId(
    "equipment",
    ItemContainerStackMode.Default,
    EQUIPMENT_SIZE,
    EQUIPMENT_ID
)

internal val bankKey = ItemContainerKey.ofClientId(
    "bank",
    ItemContainerStackMode.Always,
    BANK_SIZE,
    BANK_ID
)

internal fun Player.setDefaultContainers() {
    setContainer(inventoryKey, inventory)
    setContainer(equipmentKey, equipment)
    setContainer(bankKey, bank)

    /* enable auto-updates for necessary containers */
    inventory.autoUpdate = true
    equipment.autoUpdate = true
}
