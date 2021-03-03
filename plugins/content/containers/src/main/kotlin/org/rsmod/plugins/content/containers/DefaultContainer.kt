package org.rsmod.plugins.content.containers

import org.rsmod.game.model.item.container.ItemContainerKey
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.model.mob.player.sendItemContainer

private const val INVENTORY_SIZE = 28
private const val EQUIPMENT_SIZE = 12
private const val BANK_SIZE = 800

private const val INVENTORY_ID = 93
private const val EQUIPMENT_ID = 94
private const val BANK_ID = 95

/*
 * These "default containers" are cached for each player
 * and do not need to be exposed publicly.
 */
internal val inventoryKey = ItemContainerKey()
internal val equipmentKey = ItemContainerKey()
internal val bankKey = ItemContainerKey()

private val inventoryComponent = Component(149, 0)

internal fun Player.setDefaultContainers() {
    setContainer(inventoryKey, inventory, INVENTORY_SIZE)
    setContainer(equipmentKey, equipment, EQUIPMENT_SIZE)
    setContainer(bankKey, bank, BANK_SIZE)
}

internal fun Player.setContainer(
    key: ItemContainerKey,
    container: ItemContainer,
    size: Int
) {
    containers[key] = container
    container.ensureCapacity(size)
}

internal fun Player.updateFullItemContainer(key: ItemContainerKey, container: ItemContainer) {
    when (key) {
        inventoryKey -> sendItemContainer(INVENTORY_ID, inventoryComponent, container)
        equipmentKey -> sendItemContainer(key = EQUIPMENT_ID, container = container)
        bankKey -> sendItemContainer(key = BANK_ID, container = bank)
    }
}

internal fun Player.updateDefaultContainer(key: ItemContainerKey, container: ItemContainer) {
    // TODO: send partial update
    updateFullItemContainer(key, container)
}
