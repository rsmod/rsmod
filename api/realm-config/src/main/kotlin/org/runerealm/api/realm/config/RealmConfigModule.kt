package org.runerealm.api.realm.config

import org.runerealm.plugin.module.PluginModule
import org.runerealm.server.services.Service

public class RealmConfigModule : PluginModule() {
    override fun bind() {
        bindInstance<RealmConfigLoader>()
        addSetBinding<Service>(RealmConfigService::class.java)
    }
}
