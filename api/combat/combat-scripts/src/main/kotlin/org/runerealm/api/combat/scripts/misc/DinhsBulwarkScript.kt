package org.runerealm.api.combat.scripts.misc

import org.runerealm.api.config.refs.categories
import org.runerealm.api.config.refs.spotanims
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.righthand
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.script.advanced.onWearposChange
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.isType
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.game.type.obj.Wearpos
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
