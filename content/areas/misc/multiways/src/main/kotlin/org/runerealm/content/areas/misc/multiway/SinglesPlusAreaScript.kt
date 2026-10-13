package org.runerealm.content.areas.misc.multiway

import org.runerealm.api.config.refs.areas
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.onArea
import org.runerealm.api.script.onAreaExit
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class SinglesPlusAreaScript : PluginScript() {
    private var ProtectedAccess.singlesPlus by boolVarBit(varbits.singleway_plus_indicator)

    override fun ScriptContext.startup() {
        onArea(areas.singles_plus) { singlesPlus = true }
        onAreaExit(areas.singles_plus) { singlesPlus = false }
    }
}
