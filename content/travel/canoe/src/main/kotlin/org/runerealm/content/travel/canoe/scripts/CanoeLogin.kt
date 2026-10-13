package org.runerealm.content.travel.canoe.scripts

import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.content.travel.canoe.configs.canoe_varbits
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class CanoeLogin : PluginScript() {
    private var Player.canoeType by intVarBit(canoe_varbits.canoe_type)
    private var Player.canoeAvoidIf by intVarBit(canoe_varbits.canoe_avoid_if)
    private var Player.canoeStation by intVarBit(canoe_varbits.current_station)

    override fun ScriptContext.startup() {
        onPlayerLogin { player.resetCanoeVars() }
    }

    private fun Player.resetCanoeVars() {
        canoeStation = 0
        canoeType = 0
        canoeAvoidIf = 0
    }
}
