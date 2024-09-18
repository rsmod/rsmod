package org.rsmod.api.shops.cost

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.shops.bowl
import org.rsmod.api.shops.cake_tin
import org.rsmod.api.shops.cost.StandardGpCostCalculations.calculateShopSellBulkParameters
import org.rsmod.api.shops.cost.StandardGpCostCalculations.calculateShopSellSingleValue
import org.rsmod.api.shops.empty_bucket
import org.rsmod.api.shops.empty_bucket_pack
import org.rsmod.api.shops.empty_jug_pack
import org.rsmod.api.shops.empty_pot
import org.rsmod.api.shops.knife
import org.rsmod.api.shops.sos_security_book
import org.rsmod.api.shops.spade
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.game.type.obj.UnpackedObjType

class StandardGpCostCalculationBuyTest {
    @TestWithArgs(WithinStockProvider::class)
    fun `calculate single cost based on within-stock quantity`(
        initialStock: Int,
        currentStock: Int,
        expectedValue: Int,
        obj: UnpackedObjType,
    ) {
        val actualValue =
            calculateShopSellSingleValue(
                currentStock = currentStock,
                initialStock = initialStock,
                baseCost = obj.cost,
                sellPercentage = LUMBRIDGE_STORE_SELL_PERCENTAGE,
                changePercentage = LUMBRIDGE_STORE_CHANGE_PERCENTAGE,
            )
        assertEquals(expectedValue, actualValue)
    }

    private object WithinStockProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            listOf(
                // NOTE: empty_pot has a 1 gp cost; can skip other 1-gp-cost objs.
                TestArgs(5, 5, 1, empty_pot),
                TestArgs(5, 4, 1, empty_pot),
                TestArgs(5, 3, 1, empty_pot),
                TestArgs(5, 2, 1, empty_pot),
                TestArgs(5, 1, 1, empty_pot),
                TestArgs(5, 0, 1, empty_pot),
                TestArgs(5, 5, 182, empty_jug_pack),
                TestArgs(5, 4, 186, empty_jug_pack),
                TestArgs(5, 3, 190, empty_jug_pack),
                TestArgs(5, 2, 194, empty_jug_pack),
                TestArgs(5, 1, 198, empty_jug_pack),
                TestArgs(5, 0, 203, empty_jug_pack),
                TestArgs(5, 5, 7, knife),
                TestArgs(5, 4, 7, knife),
                TestArgs(5, 3, 8, knife),
                TestArgs(5, 2, 8, knife),
                TestArgs(5, 1, 8, knife),
                TestArgs(5, 0, 8, knife),
                TestArgs(3, 3, 2, empty_bucket),
                TestArgs(2, 2, 2, empty_bucket),
                TestArgs(2, 1, 2, empty_bucket),
                TestArgs(2, 0, 2, empty_bucket),
                TestArgs(15, 15, 650, empty_bucket_pack),
                TestArgs(15, 14, 665, empty_bucket_pack),
                TestArgs(15, 13, 680, empty_bucket_pack),
                TestArgs(15, 12, 695, empty_bucket_pack),
                TestArgs(15, 11, 710, empty_bucket_pack),
                TestArgs(15, 10, 725, empty_bucket_pack),
                TestArgs(15, 9, 740, empty_bucket_pack),
                TestArgs(15, 8, 755, empty_bucket_pack),
                TestArgs(15, 7, 770, empty_bucket_pack),
                TestArgs(15, 6, 785, empty_bucket_pack),
                TestArgs(15, 5, 800, empty_bucket_pack),
                TestArgs(15, 4, 815, empty_bucket_pack),
                TestArgs(15, 3, 830, empty_bucket_pack),
                TestArgs(15, 2, 845, empty_bucket_pack),
                TestArgs(15, 1, 860, empty_bucket_pack),
                TestArgs(15, 0, 875, empty_bucket_pack),
                TestArgs(2, 2, 5, bowl),
                TestArgs(2, 1, 5, bowl),
                TestArgs(2, 0, 5, bowl),
                TestArgs(2, 2, 13, cake_tin),
                TestArgs(2, 1, 13, cake_tin),
                TestArgs(2, 0, 13, cake_tin),
                TestArgs(5, 5, 3, spade),
                TestArgs(5, 4, 3, spade),
                TestArgs(5, 3, 4, spade),
                TestArgs(5, 2, 4, spade),
                TestArgs(5, 1, 4, spade),
                TestArgs(5, 0, 4, spade),
                TestArgs(5, 5, 2, sos_security_book),
                TestArgs(5, 4, 2, sos_security_book),
                TestArgs(5, 3, 2, sos_security_book),
                TestArgs(5, 2, 2, sos_security_book),
                TestArgs(5, 1, 2, sos_security_book),
                TestArgs(5, 0, 2, sos_security_book),
            )
    }

    @TestWithArgs(OverStockProvider::class)
    fun `calculate single cost based on overstock quantity`(
        initialStock: Int,
        currentStock: Int,
        expectedValue: Int,
        obj: UnpackedObjType,
    ) {
        val actualValue =
            calculateShopSellSingleValue(
                currentStock = currentStock,
                initialStock = initialStock,
                baseCost = obj.cost,
                sellPercentage = LUMBRIDGE_STORE_SELL_PERCENTAGE,
                changePercentage = LUMBRIDGE_STORE_CHANGE_PERCENTAGE,
            )
        assertEquals(expectedValue, actualValue)
    }

    private object OverStockProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            listOf(
                TestArgs(5, 9, 7, knife),
                TestArgs(5, 5, 7, knife),
                TestArgs(5, 10, 6, knife),
                TestArgs(3, 3, 2, empty_bucket),
                TestArgs(3, 13, 2, empty_bucket),
                TestArgs(3, 14, 1, empty_bucket),
                TestArgs(3, Int.MAX_VALUE, 1, empty_bucket),
                TestArgs(15, 16, 635, empty_bucket_pack),
                TestArgs(15, 17, 620, empty_bucket_pack),
            )
    }

    @TestWithArgs(StockBulkProvider::class)
    fun `calculate bulk cost based on stock`(
        initialStock: Int,
        currentStock: Int,
        buyCount: Int,
        expectedTotalValue: Int,
        obj: UnpackedObjType,
    ) {
        val parameters =
            calculateShopSellBulkParameters(
                currentStock = currentStock,
                initialStock = initialStock,
                baseCost = obj.cost,
                requestedCount = buyCount,
                availableCurrency = Int.MAX_VALUE,
                sellPercentage = LUMBRIDGE_STORE_SELL_PERCENTAGE,
                changePercentage = LUMBRIDGE_STORE_CHANGE_PERCENTAGE,
            )
        assertEquals(expectedTotalValue, parameters.totalValue)
    }

    private object StockBulkProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            listOf(
                TestArgs(5, 5, 2, 14, knife),
                TestArgs(5, 5, 5, 950, empty_jug_pack),
                TestArgs(5, 5, 4, 752, empty_jug_pack),
                TestArgs(5, 5, 3, 558, empty_jug_pack),
                TestArgs(5, 5, 2, 368, empty_jug_pack),
                TestArgs(5, 5, 1, 182, empty_jug_pack),
                TestArgs(5, 5, 0, 0, empty_jug_pack),
                TestArgs(15, 15, 15, 11_325, empty_bucket_pack),
                TestArgs(15, 14, 14, 10_675, empty_bucket_pack),
                TestArgs(15, 13, 13, 10_010, empty_bucket_pack),
                TestArgs(15, 12, 12, 9330, empty_bucket_pack),
                TestArgs(15, 11, 11, 8635, empty_bucket_pack),
                TestArgs(15, 10, 10, 7925, empty_bucket_pack),
                TestArgs(15, 9, 9, 7200, empty_bucket_pack),
                TestArgs(15, 8, 8, 6460, empty_bucket_pack),
                TestArgs(15, 7, 7, 5705, empty_bucket_pack),
                TestArgs(15, 6, 6, 4935, empty_bucket_pack),
                TestArgs(15, 5, 5, 4150, empty_bucket_pack),
                TestArgs(15, 4, 4, 3350, empty_bucket_pack),
                TestArgs(15, 3, 3, 2535, empty_bucket_pack),
                TestArgs(15, 2, 2, 1705, empty_bucket_pack),
                TestArgs(15, 1, 1, 860, empty_bucket_pack),
                TestArgs(15, 15, 10, 7175, empty_bucket_pack),
                TestArgs(15, 15, 5, 3400, empty_bucket_pack),
            )
    }

    private companion object {
        const val LUMBRIDGE_STORE_SELL_PERCENTAGE: Double = 130.0
        const val LUMBRIDGE_STORE_CHANGE_PERCENTAGE: Double = 3.0
    }
}
