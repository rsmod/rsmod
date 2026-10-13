package org.runerealm.api.shops.restock

import jakarta.inject.Inject
import org.runerealm.api.game.process.GameLifecycle
import org.runerealm.api.script.onEvent
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class ShopRestockScript @Inject constructor(private val restockProcess: ShopRestockProcess) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onEvent<GameLifecycle.LateCycle> { restock() }
    }

    private fun restock() {
        restockProcess.process()
    }
}
