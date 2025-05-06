package org.rsmod.api.realm.config.updater

import org.rsmod.plugin.module.PluginModule

internal class RealmConfigUpdaterModule : PluginModule() {
    override fun bind() {
        bindInstance<RealmConfigUpdater>()
    }
}
