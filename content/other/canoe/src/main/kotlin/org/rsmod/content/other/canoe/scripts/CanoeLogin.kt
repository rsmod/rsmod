package org.rsmod.content.other.canoe.scripts

import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.content.other.canoe.configs.canoe_varbits
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
