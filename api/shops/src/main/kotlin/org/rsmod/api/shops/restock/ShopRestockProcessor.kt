package org.rsmod.api.shops.restock

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.game.MapClock
import org.rsmod.game.inv.Inventory

public class ShopRestockProcessor @Inject constructor(private val mapClock: MapClock) {
    public val modifiedShops: MutableSet<Inventory> = hashSetOf()

    private val restockedShops: MutableSet<Inventory> = hashSetOf()

    private val logger = InlineLogger()

    public operator fun plusAssign(inventory: Inventory) {
        modifiedShops += inventory
    }

    public fun process() {
        processModifiedShops()
        removeRestockedShops()
    }

    private fun processModifiedShops() {
        for (shop in modifiedShops) {
            val fullyRestocked = shop.restoreInitialStock()
            if (fullyRestocked) {
                restockedShops += shop
            }
        }
    }

    private fun Inventory.restoreInitialStock(): Boolean {
        var fullyRestocked = true
        val stock = type.stock
        for (i in indices) {
            val obj = this[i] ?: continue
            val stockObj = stock?.getOrNull(i)

            val initialStock = stockObj?.count ?: 0
            if (obj.count == initialStock) {
                continue
            }

            val restockRate = stockObj?.restockCycles ?: NON_STOCK_NORMALIZE_CYCLES
            if (mapClock % restockRate != 0) {
                fullyRestocked = false
                continue
            }

            val newCount = if (obj.count > initialStock) obj.count - 1 else obj.count + 1
            val deleteObj = newCount == 0 && stockObj == null
            if (deleteObj) {
                this[i] = null
            } else {
                this[i] = obj.copy(count = newCount)
            }

            if (newCount != initialStock) {
                fullyRestocked = false
            }
        }
        return fullyRestocked
    }

    private fun removeRestockedShops() {
        if (restockedShops.isNotEmpty()) {
            val neutralizeCount = restockedShops.size
            modifiedShops.removeAll(restockedShops)
            restockedShops.clear()
            logClearedRestockedShops(modifiedShops.size, neutralizeCount)
        }
    }

    private fun logClearedRestockedShops(modifiedCount: Int, neutralizeCount: Int) {
        if (!logger.isDebugEnabled) {
            return
        }
        val message =
            if (modifiedCount == 0) {
                "Cleared ${neutralizeCount.formatAmount} modified " +
                    "shop${if (neutralizeCount == 1) "" else "s"}."
            } else {
                "Cleared ${neutralizeCount.formatAmount} modified shops. $modifiedCount " +
                    "active modified shop${if (modifiedCount == 1) "" else "s"} left."
            }
        logger.debug { message }
    }

    private companion object {
        private const val NON_STOCK_NORMALIZE_CYCLES: Int = 100
    }
}
