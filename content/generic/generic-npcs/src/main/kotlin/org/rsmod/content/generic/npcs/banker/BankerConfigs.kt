@file:Suppress("ConstPropertyName")

package org.rsmod.content.generic.npcs.banker

import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences

internal typealias banker_varbits = BankerVarBits

internal typealias banker_enums = BankerEnums

object BankerVarBits : VarBitReferences() {
    val blocks_purchased = find("bank_space_blocks_purchased", 54200306000469)
}

object BankerEnums : EnumReferences() {
    val block_costs = find<Int, Int>("bank_space_purchase_block_cost")
}

internal object BankerEnumBuilder : EnumBuilder() {
    init {
        build<Int, Int>("bank_space_purchase_block_cost") {
            this[1] = 1_000_000
            this[2] = 2_000_000
            this[3] = 5_000_000
            this[4] = 10_000_000
            this[5] = 20_000_000
            this[6] = 50_000_000
            this[7] = 100_000_000
            this[8] = 200_000_000
            this[9] = 500_000_000
            default = 999_999_999
        }
    }
}
