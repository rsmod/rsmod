package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.script.onPlayerInit
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class InvInitScript @Inject constructor(private val invInit: InvInit) : PluginScript() {
    override fun ScriptContext.startUp() {
        onPlayerInit { invInit.init(player) }
    }
}
