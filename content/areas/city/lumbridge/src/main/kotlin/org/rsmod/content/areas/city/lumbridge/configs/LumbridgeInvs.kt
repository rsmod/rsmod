package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.inv.InvEditor
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

object LumbridgeInvs : InvReferences() {
    val axeshop = find("axeshop")
}

object LumbridgeInvBuilder : InvEditor() {
    init {
        edit("axeshop") {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(objs.bronze_pickaxe, count = 5, restockCycles = 100)
            stock += stock(objs.bronze_axe, count = 10, restockCycles = 100)
            stock += stock(objs.iron_axe, count = 5, restockCycles = 200)
            stock += stock(objs.steel_axe, count = 3, restockCycles = 400)
            stock += stock(objs.iron_battleaxe, count = 5, restockCycles = 100)
            stock += stock(objs.steel_battleaxe, count = 2, restockCycles = 200)
            stock += stock(objs.mithril_battleaxe, count = 1, restockCycles = 3000)
        }
    }
}
