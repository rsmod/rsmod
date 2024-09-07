package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.script.onPlayerInit
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.plugin.scripts.SimplePluginScript

public class InvInitScript @Inject constructor(private val invInit: InvInit) :
    SimplePluginScript() {
    override fun ScriptContext.startUp() {
        onPlayerInit { invInit.init(player) }
    }
}
