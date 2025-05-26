// package org.rsmod.content.items.food.scripts
//
// import org.rsmod.api.config.refs.BaseStats.hitpoints
// import org.rsmod.api.config.refs.content
// import org.rsmod.api.config.refs.params
// import org.rsmod.api.config.refs.queues
// import org.rsmod.api.player.protect.ProtectedAccess
// import org.rsmod.api.player.stat.statHeal
// import org.rsmod.api.script.onOpHeld1
// import org.rsmod.api.script.onPlayerQueueWithArgs
// import org.rsmod.content.items.food.configs.canEat
// import org.rsmod.content.items.food.configs.eat
// import org.rsmod.content.items.food.configs.heal
// import org.rsmod.content.items.food.configs.healOverTime
// import org.rsmod.game.inv.InvObj
// import org.rsmod.game.type.obj.UnpackedObjType
// import org.rsmod.plugin.scripts.PluginScript
// import org.rsmod.plugin.scripts.ScriptContext
//
// class DrinkPotion : PluginScript() {
//
//    override fun ScriptContext.startup() {
//        onOpHeld1(content.potion) { this.drinkPotion(it.obj, it.type, it.slot) }
//
//    }
//
//    private fun ProtectedAccess.drinkPotion(item: InvObj, type: UnpackedObjType, slot: Int) {
//        if (!canDrink(type, player)) {
//            return
//        }
//        drink(item, type, slot)
//        }
//    }
// }
