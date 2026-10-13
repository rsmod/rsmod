package org.runerealm.api.core.module

import com.google.inject.Provider
import jakarta.inject.Inject
import org.runerealm.api.realm.Realm
import org.runerealm.api.server.config.ServerConfig
import org.runerealm.module.ExtendedModule

public object RealmModule : ExtendedModule() {
    override fun bind() {
        bindProvider(RealmProvider::class.java)
    }

    private class RealmProvider @Inject constructor(private val config: ServerConfig) :
        Provider<Realm> {
        override fun get(): Realm = Realm(config.realm)
    }
}
