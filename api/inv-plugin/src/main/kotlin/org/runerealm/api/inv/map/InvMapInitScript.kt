package org.runerealm.api.inv.map

import jakarta.inject.Inject
import org.runerealm.api.script.onPlayerInit
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class InvMapInitScript @Inject constructor(private val invMapInit: InvMapInit) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerInit { invMapInit.init(player) }
    }
}
