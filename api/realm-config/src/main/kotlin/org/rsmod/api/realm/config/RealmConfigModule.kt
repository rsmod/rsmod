package org.rsmod.api.realm.config

import org.rsmod.plugin.module.PluginModule
import org.rsmod.server.services.Service

public class RealmConfigModule : PluginModule() {
    override fun bind() {
        bindInstance<RealmConfigLoader>()
        addSetBinding<Service>(RealmConfigService::class.java)
    }
}
