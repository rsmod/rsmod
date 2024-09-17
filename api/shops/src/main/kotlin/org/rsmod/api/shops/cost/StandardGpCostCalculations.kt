package org.rsmod.api.shops.cost

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

public object StandardGpCostCalculations {
    public const val BUY_FROM_SHOP_MIN_FRACTION: Double = 0.3
    public const val SELL_TO_SHOP_MIN_FRACTION: Double = 0.1

    public data class PriceParameters(
        val basePriceWithMarkup: Int,
        val stockDifference: Int,
        val firstObjPrice: Int,
        val priceIncreasePerObj: Int,
    )

    public fun calculatePriceParameters(
        currentStock: Int,
        initialStock: Int,
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

        val priceIncreasePerObj = max(1, (baseCost * (changePercentage / 100.0)).toInt())
        return PriceParameters(
            basePriceWithMarkup = basePriceWithMarkup.toInt(),
            stockDifference = stockDifference.toInt(),
            firstObjPrice = firstObjPrice.toInt(),
            priceIncreasePerObj = priceIncreasePerObj,
        )
    }

    public fun calculateSingleSaleValue(
        currentStock: Int,
        initialStock: Int,
        baseCost: Int,
        sellPercentage: Double,
        changePercentage: Double,
    ): Int {
        val parameters =
            calculatePriceParameters(
                currentStock = currentStock,
                initialStock = initialStock,
                baseCost = baseCost,
                exchangePercentage = sellPercentage,
                changePercentage = changePercentage,
                cap = BUY_FROM_SHOP_MIN_FRACTION,
                minCost = 1,
            )
        return parameters.firstObjPrice
    }

    public fun calculateSingleBuyValue(
        currentStock: Int,
        initialStock: Int,
        baseCost: Int,
        buyPercentage: Double,
        changePercentage: Double,
    ): Int {
        val parameters =
            calculatePriceParameters(
                currentStock = currentStock,
                initialStock = initialStock,
                baseCost = baseCost,
                exchangePercentage = buyPercentage,
                changePercentage = changePercentage,
                cap = SELL_TO_SHOP_MIN_FRACTION,
                minCost = 0,
            )
        return parameters.firstObjPrice
    }

    public data class BulkSaleParameters(
        val count: Int,
        val totalCost: Int,
        val firstObjPrice: Int,
    )

    public fun calculateBulkSaleParameters(
        currentStock: Int,
        initialStock: Int,
        baseCost: Int,
        requestedCount: Int,
        availableObjCount: Int,
        availableCurrency: Int,
        sellPercentage: Double,
        changePercentage: Double,
    ): BulkSaleParameters =
        calculateBulkPriceParameters(
            currentStock = currentStock,
            initialStock = initialStock,
            baseCost = baseCost,
            requestedCount = requestedCount,
            availableObjCount = availableObjCount,
            availableCurrency = availableCurrency,
            exchangePercentage = sellPercentage,
            changePercentage = changePercentage,
            cap = BUY_FROM_SHOP_MIN_FRACTION,
            minCost = 1,
        )

    public fun calculateBulkBuyParameters(
        currentStock: Int,
        initialStock: Int,
        baseCost: Int,
        requestedCount: Int,
        availableObjCount: Int,
        availableCurrency: Int,
        buyPercentage: Double,
        changePercentage: Double,
    ): BulkSaleParameters =
        calculateBulkPriceParameters(
            currentStock = currentStock,
            initialStock = initialStock,
            baseCost = baseCost,
            requestedCount = requestedCount,
            availableObjCount = availableObjCount,
            availableCurrency = availableCurrency,
            exchangePercentage = buyPercentage,
            changePercentage = changePercentage,
            cap = SELL_TO_SHOP_MIN_FRACTION,
            minCost = 0,
        )

    public fun calculateBulkPriceParameters(
        currentStock: Int,
        initialStock: Int,
        baseCost: Int,
        requestedCount: Int,
        availableObjCount: Int,
        availableCurrency: Int,
        exchangePercentage: Double,
        changePercentage: Double,
        cap: Double,
        minCost: Int,
    ): BulkSaleParameters {
        val priceParams =
            calculatePriceParameters(
                currentStock = currentStock,
                initialStock = initialStock,
                baseCost = baseCost,
                exchangePercentage = exchangePercentage,
                changePercentage = changePercentage,
                cap = cap,
                minCost = minCost,
            )
        val firstObjPrice = priceParams.firstObjPrice
        val priceIncreasePerObj = priceParams.priceIncreasePerObj

        val quadraticA = priceIncreasePerObj
        val quadraticB = 2L * firstObjPrice - priceIncreasePerObj
        val quadraticC = -2L * availableCurrency

        val discriminant = quadraticB * quadraticB - 4L * quadraticA * quadraticC
        if (discriminant < 0) {
            return BulkSaleParameters(count = 0, totalCost = 0, firstObjPrice = firstObjPrice)
        }

        val validRequestCount = min(requestedCount, availableObjCount)
        var purchaseCount =
            floor((-quadraticB + sqrt(discriminant.toDouble())) / (2 * quadraticA)).toInt()
        purchaseCount = max(0, min(validRequestCount, purchaseCount))

        var totalCost = calculateTotalCost(purchaseCount, firstObjPrice, priceIncreasePerObj)
        while (totalCost > availableCurrency && purchaseCount > 0) {
            purchaseCount--
            totalCost = calculateTotalCost(purchaseCount, firstObjPrice, priceIncreasePerObj)
        }

        return BulkSaleParameters(
            count = purchaseCount,
            totalCost = totalCost,
            firstObjPrice = firstObjPrice,
        )
    }

    public fun calculateTotalCost(
        objCount: Int,
        firstObjPrice: Int,
        priceIncreasePerObj: Int,
    ): Int = objCount * (2 * firstObjPrice + (objCount - 1) * priceIncreasePerObj) / 2
}
