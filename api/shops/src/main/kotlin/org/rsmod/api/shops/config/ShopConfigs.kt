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
    public val shop_main: InterfaceType = find(9223372035530134635)
    public val shop_side: InterfaceType = find(9223372034793400280)
}

public object ShopComponents : ComponentReferences() {
    public val shop_subtext: ComponentType = find(1009675651464801228)
    public val shop_side_inv: ComponentType = find(5117171527864918016)
    public val shop_inv: ComponentType = find(7875443253800243706)
}

public object ShopParams : ParamReferences() {
    public val shop_sell_percentage: ParamInt = find(106686850953)
    public val shop_buy_percentage: ParamInt = find(94225594054)
    public val shop_change_percentage: ParamInt = find(89102632885)
}

public object ShopParamBuilder : ParamBuilder() {
    init {
        // Values are multiplied by 10 for "decimal precision".
        build<Int>("shop_sell_percentage") { default = 1300 }
        build<Int>("shop_buy_percentage") { default = 400 }
        build<Int>("shop_change_percentage") { default = 30 }
    }
}
