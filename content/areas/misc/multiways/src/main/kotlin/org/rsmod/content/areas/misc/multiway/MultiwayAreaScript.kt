package org.rsmod.content.areas.misc.multiway

import org.rsmod.api.config.refs.areas
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.script.onArea
import org.rsmod.api.script.onAreaExit
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class MultiwayAreaScript : PluginScript() {
    private var ProtectedAccess.multiway by boolVarBit(varbits.multiway_indicator)

    override fun ScriptContext.startup() {
        onArea(areas.multiway) { multiway = true }
        onAreaExit(areas.multiway) { multiway = false }
    }
}
