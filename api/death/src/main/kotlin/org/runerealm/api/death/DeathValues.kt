package org.runerealm.api.death

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.runerealm.api.config.refs.params
import org.runerealm.api.death.configs.death_params
import org.runerealm.api.market.MarketPrices
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.UnpackedObjType

/**
 * Item values and fees used on death.
 *
 * Per the wiki ("Items Kept on Death"), kept items are chosen "by effective Grand Exchange value,
 * and then by alchemy value if the item lacks an effective Grand Exchange value". [MarketPrices]
 * supplies the Grand Exchange price; objs without one fall back to their store value.
 */
@Singleton
public class DeathValues
@Inject
constructor(private val objTypes: ObjTypeList, private val marketPrices: MarketPrices) {
    /** The value of a single [obj] (not the whole stack). */
    public fun unitValue(obj: InvObj): Long = unitValue(objTypes[obj])

    public fun unitValue(type: UnpackedObjType): Long {
        val uncert = objTypes.uncert(type)
        val price = marketPrices[uncert] ?: uncert.cost
        return price.toLong().coerceAtLeast(0)
    }

    /** Whether [type] counts as tradeable for death purposes (e.g. dropping to a PKer). */
    public fun isTradeable(type: UnpackedObjType): Boolean {
        if (!type.tradeable) {
            return false
        }
        // The cache does not flag most untradeables. Objs that can be traded on the Grand
        // Exchange, noted, or stacked (coins, runes, tokens) are tradeable; others are not.
        return type.stockmarket || type.canCert || type.isCert || type.isStackable
    }

    public fun isAlwaysKept(type: UnpackedObjType): Boolean =
        type.param(death_params.keep) || type.param(params.bond_item)

    public fun isAlwaysDeleted(type: UnpackedObjType): Boolean = type.param(death_params.delete)

    /**
     * The fee to take [objs] from a gravestone, per the wiki's "Item recovery fees": free under
     * 100k, then 1k / 10k / 100k per item worth 100k+ / 1m+ / 10m+, capped at 500k in total.
     * Ironmen pay half.
     */
    public fun gravestoneFee(objs: Iterable<InvObj>, ironman: Boolean): Int {
        var fee = 0L
        for (obj in objs) {
            val tier =
                when (unitValue(obj)) {
                    in 0 until 100_000 -> 0L
                    in 100_000 until 1_000_000 -> 1_000L
                    in 1_000_000 until 10_000_000 -> 10_000L
                    else -> 100_000L
                }
            fee += tier * obj.count
        }
        fee = fee.coerceAtMost(GRAVESTONE_FEE_CAP)
        return discount(fee, ironman)
    }

    /**
     * The fee to reclaim [objs] from Death's Office: 5% of the value of items worth 100k or more.
     * Ironmen pay half (2.5%).
     */
    public fun deathOfficeFee(objs: Iterable<InvObj>, ironman: Boolean): Int {
        var fee = 0L
        for (obj in objs) {
            val value = unitValue(obj)
            if (value >= FREE_RECLAIM_THRESHOLD) {
                fee += value * obj.count * DEATH_OFFICE_FEE_PERCENT / 100
            }
        }
        return discount(fee, ironman)
    }

    private fun discount(fee: Long, ironman: Boolean): Int {
        val discounted = if (ironman) fee / 2 else fee
        return discounted.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }

    private companion object {
        const val FREE_RECLAIM_THRESHOLD = 100_000L
        const val GRAVESTONE_FEE_CAP = 500_000L
        const val DEATH_OFFICE_FEE_PERCENT = 5L
    }
}
