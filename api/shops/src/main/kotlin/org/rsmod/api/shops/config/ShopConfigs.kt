@file:Suppress("PropertyName")

package org.rsmod.api.shops.config

import org.rsmod.api.config.aliases.ParamInt
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences
import org.rsmod.api.type.refs.param.ParamReferences
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.InterfaceType

public object ShopInterfaces : InterfaceReferences() {
    public val shop_main: InterfaceType = find("shopmain", 1596431697)
    public val shop_side: InterfaceType = find("shopside", 9223372034793400280)
}

public object ShopComponents : ComponentReferences() {
    public val shop_subtext: ComponentType =
        find("shopmain:desktop_instructions", 1009675651464801228)
    public val shop_side_inv: ComponentType = find("shopside:items", 5117171527864918016)
    public val shop_inv: ComponentType = find("shopmain:items", 7875443253800243706)
}

public object ShopParams : ParamReferences() {
    public val shop_sell_percentage: ParamInt = find("shop_sell_percentage")
    public val shop_buy_percentage: ParamInt = find("shop_buy_percentage")
    public val shop_change_percentage: ParamInt = find("shop_change_percentage")
}

public object ShopParamBuilder : ParamBuilder() {
    init {
        // Values are multiplied by 10 for "decimal precision".
        build<Int>("shop_sell_percentage") { default = 1300 }
        build<Int>("shop_buy_percentage") { default = 400 }
        build<Int>("shop_change_percentage") { default = 30 }
    }
}
