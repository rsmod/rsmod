package org.rsmod.api.shops.cost

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.shops.amethyst_arrow
import org.rsmod.api.shops.amulet_of_eternal_glory
import org.rsmod.api.shops.bowl
import org.rsmod.api.shops.cake_tin
import org.rsmod.api.shops.cost.StandardGpCostCalculations.calculateShopBuyBulkParameters
import org.rsmod.api.shops.cost.StandardGpCostCalculations.calculateShopBuySingleValue
import org.rsmod.api.shops.dragon_claws
import org.rsmod.api.shops.empty_bucket_pack
import org.rsmod.api.shops.empty_jug_pack
import org.rsmod.api.shops.empty_pot
import org.rsmod.api.shops.knife
import org.rsmod.api.shops.shark
import org.rsmod.api.shops.spade
import org.rsmod.api.shops.stamina_potion4
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.game.type.obj.UnpackedObjType

class StandardGpCostCalculationSellTest {
    @TestWithArgs(StockObjProvider::class)
    fun `calculate single cost for shop-stocked obj`(
        initialStock: Int,
        currentStock: Int,
        expectedValue: Int,
        obj: UnpackedObjType,
    ) {
        val actualValue =
            calculateShopBuySingleValue(
                currentStock = currentStock,
                initialStock = initialStock,
                baseCost = obj.cost,
                buyPercentage = LUMBRIDGE_STORE_BUY_PERCENTAGE,
                changePercentage = LUMBRIDGE_STORE_CHANGE_PERCENTAGE,
            )
        assertEquals(expectedValue, actualValue)
    }

    private object StockObjProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            listOf(
                // NOTE: empty_pot has a 1 gp cost; can skip other 1-gp-cost objs.
                TestArgs(5, 5, 0, empty_pot),
                TestArgs(5, 5, 56, empty_jug_pack),
                TestArgs(5, 5, 2, knife),
                TestArgs(15, 15, 200, empty_bucket_pack),
                TestArgs(2, 2, 1, bowl),
                TestArgs(2, 2, 4, cake_tin),
                TestArgs(5, 5, 1, spade),
            )
    }

    @TestWithArgs(NonStockObjProvider::class)
    fun `calculate single cost for non-stock obj`(
        currentStock: Int,
        expectedValue: Int,
        obj: UnpackedObjType,
    ) {
        val actualValue =
            calculateShopBuySingleValue(
                currentStock = currentStock,
                initialStock = 0,
                baseCost = obj.cost,
                buyPercentage = LUMBRIDGE_STORE_BUY_PERCENTAGE,
                changePercentage = LUMBRIDGE_STORE_CHANGE_PERCENTAGE,
            )
        assertEquals(expectedValue, actualValue)
    }

    private object NonStockObjProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            listOf(
                TestArgs(0, 68, shark),
                TestArgs(1, 62, shark),
                TestArgs(2, 57, shark),
                TestArgs(3, 52, shark),
                TestArgs(4, 47, shark),
                TestArgs(5, 42, shark),
                TestArgs(6, 37, shark),
                TestArgs(0, 160, stamina_potion4),
                TestArgs(0, 82_000, dragon_claws),
                TestArgs(0, 7050, amulet_of_eternal_glory),
            )
    }

    @Test
    fun `calculate bulk sale carrying max cash`() {
        val parameters =
            calculateShopBuyBulkParameters(
                currentStock = 0,
                initialStock = 0,
                baseCost = 100,
                requestedCount = 5,
                currencyCap = 0,
                buyPercentage = LUMBRIDGE_STORE_BUY_PERCENTAGE,
                changePercentage = LUMBRIDGE_STORE_CHANGE_PERCENTAGE,
            )
        assertEquals(0, parameters.totalValue)
        assertEquals(0, parameters.count)
        assertEquals(40, parameters.firstObjPrice)
    }

    @Test
    fun `calculate bulk sale carrying near max cash`() {
        val parameters =
            calculateShopBuyBulkParameters(
                currentStock = 0,
                initialStock = 0,
                baseCost = 100,
                requestedCount = 5,
                currencyCap = 50,
                buyPercentage = LUMBRIDGE_STORE_BUY_PERCENTAGE,
                changePercentage = LUMBRIDGE_STORE_CHANGE_PERCENTAGE,
            )
        assertEquals(40, parameters.totalValue)
        assertEquals(40, parameters.firstObjPrice)
        assertEquals(1, parameters.count)
    }

    @TestWithArgs(BulkObjProvider::class)
    fun `calculate bulk value`(
        initialStock: Int,
        currentStock: Int,
        sellCount: Int,
        expectedTotalValue: Int,
        obj: UnpackedObjType,
    ) {
        val parameters =
            calculateShopBuyBulkParameters(
                currentStock = currentStock,
                initialStock = initialStock,
                baseCost = obj.cost,
                requestedCount = sellCount,
                currencyCap = Int.MAX_VALUE,
                buyPercentage = LUMBRIDGE_STORE_BUY_PERCENTAGE,
                changePercentage = LUMBRIDGE_STORE_CHANGE_PERCENTAGE,
            )
        assertEquals(expectedTotalValue, parameters.totalValue)
    }

    private object BulkObjProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            listOf(
                TestArgs(5, 5, 5, 236, empty_jug_pack),
                TestArgs(0, 0, 1, 196, amethyst_arrow),
                TestArgs(0, 1, 1, 181, amethyst_arrow),
                TestArgs(0, 2, 1, 166, amethyst_arrow),
                TestArgs(0, 3, 1, 151, amethyst_arrow),
                TestArgs(0, 4, 1, 137, amethyst_arrow),
                TestArgs(0, 5, 1, 122, amethyst_arrow),
                TestArgs(0, 0, 5, 831, amethyst_arrow),
                TestArgs(0, 0, 1, 196, amethyst_arrow),
            )
    }

    private companion object {
        const val LUMBRIDGE_STORE_BUY_PERCENTAGE: Double = 40.0
        const val LUMBRIDGE_STORE_CHANGE_PERCENTAGE: Double = 3.0
    }
}
