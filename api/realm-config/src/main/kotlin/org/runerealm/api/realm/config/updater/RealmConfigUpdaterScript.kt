package org.runerealm.api.realm.config.updater

import jakarta.inject.Inject
import org.runerealm.api.script.onGameStartup
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
