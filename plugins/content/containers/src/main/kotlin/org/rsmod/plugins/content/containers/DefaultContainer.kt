package org.rsmod.plugins.content.containers

import org.rsmod.game.model.item.container.ItemContainerKey
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component

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
internal val inventoryKey = ItemContainerKey.of(INVENTORY_ID, inventoryComponent)
internal val equipmentKey = ItemContainerKey.ofId(EQUIPMENT_ID)
internal val bankKey = ItemContainerKey.ofId(BANK_ID)

internal fun Player.setDefaultContainers() {
    setContainer(inventoryKey, inventory, INVENTORY_SIZE)
    setContainer(equipmentKey, equipment, EQUIPMENT_SIZE)
    setContainer(bankKey, bank, BANK_SIZE)

    /* enable auto-updates for necessary containers */
    inventory.autoUpdate = true
    equipment.autoUpdate = true
}

internal fun Player.setContainer(
    key: ItemContainerKey,
    container: ItemContainer,
    size: Int
) {
    container.ensureCapacity(size)
    containers[key] = container
}
