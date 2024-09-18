package org.rsmod.api.shops.cost

import kotlin.math.floor
import kotlin.math.max

public object StandardGpCostCalculations {
    public const val BUY_FROM_SHOP_MIN_FRACTION: Double = 0.3
    public const val SELL_TO_SHOP_MIN_FRACTION: Double = 0.1

    public data class PriceParameters(
        val basePriceWithMarkup: Int,
        val stockDifference: Int,
        val firstObjPrice: Int,
        val priceChangePerObj: Int,
    )

    public data class BulkPriceParameters(
        val count: Int,
        val totalValue: Int,
        val firstObjPrice: Int,
    )

    public fun calculatePriceParameters(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        exchangePercentage: Double,
        changePercentage: Double,
        cap: Double,
        minCost: Int,
    ): PriceParameters {
        val stockDifference = initialStock.toLong() - currentStock.toLong()
        val difference = stockDifference * baseCost * (changePercentage / 100.0)
        val basePriceWithMarkup = baseCost * (exchangePercentage / 100.0)
        var firstObjPrice = floor(basePriceWithMarkup + difference)
        firstObjPrice = max(minCost.toDouble(), max(firstObjPrice, baseCost * cap))

        val priceChangePerObj = max(minCost, (baseCost * (changePercentage / 100.0)).toInt())
        return PriceParameters(
            basePriceWithMarkup = basePriceWithMarkup.toInt(),
            stockDifference = stockDifference.toInt(),
            firstObjPrice = firstObjPrice.toInt(),
            priceChangePerObj = priceChangePerObj,
        )
    }

    /* Logic for buying objs from shop */

    public fun calculateShopSellSingleValue(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        sellPercentage: Double,
        changePercentage: Double,
    ): Int {
        val parameters =
            calculatePriceParameters(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                exchangePercentage = sellPercentage,
                changePercentage = changePercentage,
                cap = BUY_FROM_SHOP_MIN_FRACTION,
                minCost = 1,
            )
        return parameters.firstObjPrice
    }

    public fun calculateShopSellBulkParameters(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        requestedCount: Int,
        availableCurrency: Int,
        sellPercentage: Double,
        changePercentage: Double,
    ): BulkPriceParameters {
        val firstObjPrice =
            calculateShopSellSingleValue(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                sellPercentage = sellPercentage,
                changePercentage = changePercentage,
            )
        var currentCost = firstObjPrice
        var totalValue = 0
        var exchangeCount = 0

        while (exchangeCount < requestedCount && totalValue + currentCost <= availableCurrency) {
            totalValue += currentCost
            exchangeCount++
            currentCost =
                calculateShopSellSingleValue(
                    initialStock = initialStock,
                    currentStock = currentStock - exchangeCount,
                    baseCost = baseCost,
                    sellPercentage = sellPercentage,
                    changePercentage = changePercentage,
                )
        }

        return BulkPriceParameters(
            count = exchangeCount,
            totalValue = totalValue,
            firstObjPrice = firstObjPrice,
        )
    }

    /* Logic for selling objs to shop */

    public fun calculateShopBuySingleValue(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        buyPercentage: Double,
        changePercentage: Double,
    ): Int {
        val parameters =
            calculatePriceParameters(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                exchangePercentage = buyPercentage,
                changePercentage = changePercentage,
                cap = SELL_TO_SHOP_MIN_FRACTION,
                minCost = 0,
            )
        return parameters.firstObjPrice
    }

    public fun calculateShopBuyBulkParameters(
        initialStock: Int,
        currentStock: Int,
        baseCost: Int,
        requestedCount: Int,
        currencyCap: Int,
        buyPercentage: Double,
        changePercentage: Double,
    ): BulkPriceParameters {
        val firstObjPrice =
            calculateShopBuySingleValue(
                initialStock = initialStock,
                currentStock = currentStock,
                baseCost = baseCost,
                buyPercentage = buyPercentage,
                changePercentage = changePercentage,
            )
        var currentPrice = firstObjPrice
        var totalValue = 0
        var exchangeCount = 0

        while (exchangeCount < requestedCount && totalValue + currentPrice <= currencyCap) {
            totalValue += currentPrice
            exchangeCount++
            currentPrice =
                calculateShopBuySingleValue(
                    initialStock = initialStock,
                    currentStock = currentStock + exchangeCount,
                    baseCost = baseCost,
                    buyPercentage = buyPercentage,
                    changePercentage = changePercentage,
                )
        }

        return BulkPriceParameters(
            count = exchangeCount,
            totalValue = totalValue,
            firstObjPrice = firstObjPrice,
        )
    }
}
