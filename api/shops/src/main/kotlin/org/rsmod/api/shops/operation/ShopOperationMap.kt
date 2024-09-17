package org.rsmod.api.shops.operation

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.game.type.currency.CurrencyType

public class ShopOperationMap(
    private val map: MutableMap<Int, ShopOperations> = Int2ObjectOpenHashMap()
) {
    public fun register(key: CurrencyType, value: ShopOperations) {
        if (map.containsKey(key.id)) {
            throw IllegalStateException("CurrencyType already mapped to shop operations: $key")
        }
        map[key.id] = value
    }

    public operator fun get(key: CurrencyType): ShopOperations? = map[key.id]

    public operator fun set(key: CurrencyType, value: ShopOperations) {
        map[key.id] = value
    }
}
