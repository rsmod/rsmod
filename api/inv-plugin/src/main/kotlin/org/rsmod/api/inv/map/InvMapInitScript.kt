package org.rsmod.api.inv.map

import jakarta.inject.Inject
import org.rsmod.api.script.onPlayerInit
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class InvMapInitScript @Inject constructor(private val invMapInit: InvMapInit) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onPlayerInit { invMapInit.init(player) }
    }
}
