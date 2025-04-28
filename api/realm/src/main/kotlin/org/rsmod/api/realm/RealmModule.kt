package org.rsmod.api.realm

import com.google.inject.Provider
import jakarta.inject.Inject
import org.rsmod.api.server.config.ServerConfig
import org.rsmod.plugin.module.PluginModule

internal class RealmModule : PluginModule() {
    override fun bind() {
        bindProvider(RealmProvider::class.java)
    }

    private class RealmProvider @Inject constructor(private val config: ServerConfig) :
        Provider<Realm> {
        override fun get(): Realm = Realm(config.realm)
    }
}
