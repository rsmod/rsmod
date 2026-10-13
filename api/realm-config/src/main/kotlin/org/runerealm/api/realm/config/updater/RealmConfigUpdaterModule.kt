package org.runerealm.api.realm.config.updater

import org.runerealm.plugin.module.PluginModule

internal class RealmConfigUpdaterModule : PluginModule() {
    override fun bind() {
        bindInstance<RealmConfigUpdater>()
    }
}
