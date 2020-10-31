package gg.rsmod.plugins.api.container

import gg.rsmod.game.model.item.ItemContainerKey
import gg.rsmod.game.model.item.ItemContainer
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.ui.Component

private val inventoryKey = ItemContainerKey()
private val equipmentKey = ItemContainerKey()
private val bankKey = ItemContainerKey()

private const val INVENTORY_SIZE = 28
private const val EQUIPMENT_SIZE = 12
private const val BANK_SIZE = 800

private const val INVENTORY_ID = 93
private const val EQUIPMENT_ID = 94
private const val BANK_ID = 95

private val inventoryComponent = Component(149, 0)

internal fun Player.setContainers() {
    setContainer(inventoryKey, inventory, INVENTORY_SIZE)
    setContainer(equipmentKey, equipment, EQUIPMENT_SIZE)
    setContainer(bankKey, bank, BANK_SIZE)
}

internal fun Player.setContainer(key: ItemContainerKey, container: ItemContainer, size: Int) {
    containers[key] = container
    container.grow(size)
}

internal fun Player.updateDefaultContainer(key: ItemContainerKey, container: ItemContainer) {
    when (key) {
        inventoryKey -> sendItemContainer(INVENTORY_ID, inventoryComponent, container)
        equipmentKey -> sendItemContainer(key = EQUIPMENT_ID, container = container)
        bankKey -> sendItemContainer(key = BANK_ID, container = bank)
    }
}
