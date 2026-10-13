package org.runerealm.content.areas.city.lumbridge.configs

import org.runerealm.api.config.refs.objs
import org.runerealm.api.type.editors.inv.InvEditor
import org.runerealm.api.type.refs.inv.InvReferences
import org.runerealm.game.type.inv.InvScope
import org.runerealm.game.type.inv.InvStackType

typealias lumbridge_invs = LumbridgeInvs

object LumbridgeInvs : InvReferences() {
    val axeshop = find("axeshop")
}

object LumbridgeInvBuilder : InvEditor() {
    init {
        edit(lumbridge_invs.axeshop) {
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
