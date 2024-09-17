package org.rsmod.api.shops.restock

import jakarta.inject.Inject
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.script.onEvent
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class ShopRestockScript
@Inject
constructor(private val restockProcessor: ShopRestockProcessor) : PluginScript() {
    override fun ScriptContext.startUp() {
        onEvent<GameLifecycle.LateCycle> { restock() }
    }

    private fun restock() {
        restockProcessor.process()
    }
}
