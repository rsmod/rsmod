package org.runerealm.content.areas.misc.multiway

import org.runerealm.api.config.refs.areas
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.onArea
import org.runerealm.api.script.onAreaExit
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class MultiwayAreaScript : PluginScript() {
    private var ProtectedAccess.multiway by boolVarBit(varbits.multiway_indicator)

    override fun ScriptContext.startup() {
        onArea(areas.multiway) { multiway = true }
        onAreaExit(areas.multiway) { multiway = false }
    }
}
