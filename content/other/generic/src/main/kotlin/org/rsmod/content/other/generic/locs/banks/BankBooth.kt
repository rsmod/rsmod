package org.rsmod.content.other.generic.locs.banks

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc2
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BankBooth : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc2(content.bank_booth) { openBank() }
    }

    private fun ProtectedAccess.openBank() {
        ifOpenMainSidePair(main = interfaces.bank_main, side = interfaces.bank_side)
    }
}
