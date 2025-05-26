package org.rsmod.api.shops.operation

import jakarta.inject.Inject
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.invtx.invTransaction
import org.rsmod.api.invtx.select
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.shops.cost.StandardGpCostCalculations
import org.rsmod.api.shops.restock.ShopRestockProcess
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.Inventory
import org.rsmod.game.shop.Shop
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.objtx.TransactionResult

private typealias CostCalculation = StandardGpCostCalculations

public class StandardGpShopOperations
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val restockProcess: ShopRestockProcess,
    private val marketPrices: MarketPrices,
) : StandardShopOperations {
    private val currencyObj: UnpackedObjType by lazy { objTypes[objs.coins] }

    override fun examineShopValue(player: Player, shop: Shop, slot: Int) {
        val obj = shop.inv[slot] ?: return
        val objType = objTypes[obj]

        val shopInitialObjCount = shop.inv.initialStockCount(obj)
        val value =
            CostCalculation.calculateShopSellSingleValue(
                initialStock = shopInitialObjCount,
                currentStock = obj.count,
                baseCost = objType.cost,
                sellPercentage = shop.sellPercentage,
                changePercentage = shop.changePercentage,
            )

        if (value == 1) {
            player.mes("${objType.name}: currently costs 1 coin.")
        } else {
            player.mes("${objType.name}: currently costs ${value.formatAmount} coins.")
        }
    }

    override fun shopBuy(player: Player, sideInv: Inventory, shop: Shop, slot: Int, request: Int) {
        val shopInv = shop.inv
        val obj = shopInv[slot] ?: return
        val objType = objTypes[obj]

        val initialPurchaseRequest = min(shopInv.count(obj, objType), request)
        if (initialPurchaseRequest == 0) {
            player.mes("That item is currently out of stock.")
            return
        }

        val shopInitialObjCount = shopInv.initialStockCount(obj)
        val availableCurrencyCount = sideInv.count(currencyObj)
        val cappedRequest =
            if (objType.isStackable) {
                min(Int.MAX_VALUE - sideInv.count(objType), initialPurchaseRequest)
            } else {
                min(sideInv.freeSpace(), initialPurchaseRequest)
            }

        val (finalPurchaseCount, totalCost, firstObjPrice) =
            CostCalculation.calculateShopSellBulkParameters(
                initialStock = shopInitialObjCount,
                currentStock = obj.count,
                baseCost = objType.cost,
                requestedCount = max(1, cappedRequest),
                availableCurrency = availableCurrencyCount,
                sellPercentage = shop.sellPercentage,
                changePercentage = shop.changePercentage,
            )
        val finalPurchaseCost = max(totalCost, firstObjPrice)

        val transaction =
            player.invTransaction(sideInv) {
                val inv = select(sideInv)
                val shop = select(shopInv)
                delete {
                    this.from = inv
                    this.obj = currencyObj.id
                    this.strictCount = finalPurchaseCost
                }
                delete {
                    this.from = shop
                    this.obj = obj.id
                    this.strictCount = finalPurchaseCount
                }
                insert {
                    this.into = inv
                    this.obj = obj.id
                    this.strictCount = finalPurchaseCount
                }
            }

        val success = transaction.success
        val currencyDel = transaction.results[0]
        val stockObjAdd = transaction.results.getOrNull(2)

        val message =
            when {
                currencyDel == TransactionResult.ObjNotFound -> NOT_ENOUGH_COINS
                currencyDel == TransactionResult.NotEnoughObjCount -> NOT_ENOUGH_COINS
                stockObjAdd == TransactionResult.NotEnoughSpace -> NOT_ENOUGH_INV_SPACE
                success && finalPurchaseCount < initialPurchaseRequest -> {
                    if (availableCurrencyCount <= finalPurchaseCost) {
                        NOT_ENOUGH_COINS
                    } else {
                        NOT_ENOUGH_INV_SPACE
                    }
                }
                else -> null
            }
        message?.let(player::mes)

        if (success) {
            restockProcess += shopInv
        }

        if (shopInv[slot] == null) {
            shopInv.resetDefaultStockItem(slot, objType)
        }
    }

    /**
     * Resets the count of a default stock obj to zero when it's sold out, instead of removing it
     * from the inventory. Applies only if [slot] is within the original stock indices
     * ([org.rsmod.game.type.inv.UnpackedInvType.stock]).
     */
    private fun Inventory.resetDefaultStockItem(slot: Int, objType: ObjType) {
        val defaultStockIndices = type.stock?.indices ?: return
        if (slot in defaultStockIndices) {
            this[slot] = InvObj(objType, count = 0)
        }
    }

    override fun examineInvValue(player: Player, sideInv: Inventory, shop: Shop, slot: Int) {
        val obj = sideInv[slot] ?: return
        val uncert = objTypes.uncert(obj)
        val objType = objTypes[uncert]

        val tradeable = objType.tradeable || objType.stockmarket
        if (!tradeable) {
            player.mes("You can't sell this item.")
            return
        }

        val saleRestricted = objType.param(params.shop_sale_restricted)
        if (saleRestricted) {
            player.mes("You can't sell this item to a shop.")
            return
        }

        val shopInv = shop.inv
        if (!shopInv.type.allStock && objType !in shopInv) {
            player.mes("You can't sell this item to this shop.")
            return
        }

        val shopCurrentObjCount = shopInv.count(objType)
        val shopInitialObjCount = shopInv.initialStockCount(objType)
        val value =
            CostCalculation.calculateShopBuySingleValue(
                initialStock = shopInitialObjCount,
                currentStock = shopCurrentObjCount,
                baseCost = objType.cost,
                buyPercentage = shop.buyPercentage,
                changePercentage = shop.changePercentage,
            )

        if (value == 1) {
            player.mes("${objType.name}: shop will buy for 1 coin.")
        } else {
            player.mes("${objType.name}: shop will buy for ${value.formatAmount} coins.")
        }
    }

    override fun invSell(player: Player, sideInv: Inventory, shop: Shop, slot: Int, request: Int) {
        val obj = sideInv[slot] ?: return
        val objType = objTypes[obj]
        val uncertType = objTypes.uncert(objType)

        val tradeable = uncertType.tradeable || uncertType.stockmarket
        if (!tradeable) {
            player.mes("You can't sell this item.")
            return
        }

        val saleRestricted = uncertType.param(params.shop_sale_restricted)
        if (saleRestricted) {
            player.mes("You can't sell this item to a shop.")
            return
        }

        val shopInv = shop.inv
        if (!shopInv.type.allStock && uncertType !in shopInv) {
            player.mes("You can't sell this item to this shop.")
            return
        }

        val invCappedRequest = min(sideInv.count(obj, objType), request)
        if (invCappedRequest == 0) {
            return
        }
        val shopCurrentObjCount = shopInv.count(uncertType)
        val shopInitialObjCount = shopInv.initialStockCount(uncertType)

        val currencyCount = sideInv.count(currencyObj)
        val cappedRequest = min(Int.MAX_VALUE - shopCurrentObjCount, invCappedRequest)

        val (count, payment) =
            CostCalculation.calculateShopBuyBulkParameters(
                initialStock = shopInitialObjCount,
                currentStock = shopCurrentObjCount,
                baseCost = uncertType.cost,
                requestedCount = max(1, cappedRequest),
                currencyCap = Int.MAX_VALUE - currencyCount,
                buyPercentage = shop.buyPercentage,
                changePercentage = shop.changePercentage,
            )

        val transaction =
            player.invTransaction(sideInv) {
                val inv = select(sideInv)
                val shop = select(shop.inv)
                delete {
                    this.from = inv
                    this.obj = obj.id
                    this.strictCount = count
                }
                insert {
                    this.into = shop
                    this.obj = uncertType.id
                    this.strictCount = count
                }
                if (payment > 0) {
                    insert {
                        this.into = inv
                        this.obj = currencyObj.id
                        this.strictCount = payment
                    }
                }
            }

        if (transaction.success) {
            restockProcess += shopInv
        }
    }

    override fun examineDesc(player: Player, inv: Inventory, shop: Shop, slot: Int) {
        val obj = inv[slot] ?: return
        val type = objTypes[obj]
        val marketPrice = marketPrices[type] ?: 0
        player.objExamine(type, obj.count, marketPrice)
    }

    private fun Inventory.initialStockCount(obj: InvObj): Int = initialStockCount(obj.id)

    private fun Inventory.initialStockCount(type: ObjType): Int = initialStockCount(type.id)

    private fun Inventory.initialStockCount(obj: Int): Int {
        val startStock = type.stock ?: return 0
        for (i in startStock.indices) {
            val stock = startStock[i] ?: continue
            if (stock.obj == obj) {
                return stock.count
            }
        }
        return 0
    }

    public companion object {
        public const val NOT_ENOUGH_COINS: String = "You don't have enough coins."
        public const val NOT_ENOUGH_INV_SPACE: String = "You don't have enough inventory space."
    }
}
