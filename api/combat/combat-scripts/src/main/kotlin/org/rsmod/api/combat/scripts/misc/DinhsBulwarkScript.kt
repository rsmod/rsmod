package org.rsmod.api.combat.scripts.misc

import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.righthand
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.advanced.onWearposChange
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.isType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class DinhsBulwarkScript : PluginScript() {
    private var Player.passiveDelay by intVarp(varps.dinhs_passive_delay)

    override fun ScriptContext.startup() {
        onWearposChange { player.wearposChange(wearpos, objType) }
    }

    private fun Player.wearposChange(wearpos: Wearpos, obj: UnpackedObjType) {
        if (wearpos != Wearpos.RightHand || !obj.isCategoryType(categories.dinhs_bulwark)) {
            return
        }

        val equipped = righthand.isType(obj)
        if (equipped) {
            spotanim(spotanims.dinhs_bulwark_glow_idle)
            passiveDelay = currentMapClock + 8
            return
        }
    }
}
