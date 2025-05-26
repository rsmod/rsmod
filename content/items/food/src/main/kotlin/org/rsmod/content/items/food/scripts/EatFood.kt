package org.rsmod.content.items.food.scripts

import org.rsmod.api.config.objParam
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpHeld1
import org.rsmod.content.items.food.configs.canEat
import org.rsmod.content.items.food.configs.eat
import org.rsmod.content.items.food.configs.heal
import org.rsmod.content.items.food.configs.healOverTime
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EatFood : PluginScript() {

    override fun ScriptContext.startup() {
        onOpHeld1(content.food) { this.eatFood(it.obj, it.type, it.slot) }

    }

    private suspend fun ProtectedAccess.eatFood(item: InvObj, type: UnpackedObjType, slot: Int) {
        if (!canEat(type, player)) {
            return
        }
        eat(item, type, slot)
        if (type.hasParam(params.food_secondary_heal)) {
            healOverTime(type, player)
        } else {
            heal(type, player)
        }
    }
}
