package org.rsmod.api.realm.config.updater

import jakarta.inject.Inject
import org.rsmod.api.script.onGameStartup
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class RealmConfigUpdaterScript
@Inject
constructor(private val updater: RealmConfigUpdater) : PluginScript() {
    override fun ScriptContext.startup() {
        onGameStartup { attachGameThread() }
    }

    private fun attachGameThread() {
        val thread = Thread.currentThread()
        updater.attachWriteThread(thread)
    }
}
