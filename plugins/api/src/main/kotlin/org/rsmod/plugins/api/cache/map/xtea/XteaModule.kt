package org.rsmod.plugins.api.cache.map.xtea

import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object XteaModule : AbstractModule() {

    override fun configure() {
        bind(XteaRepository::class.java)
            .toProvider(XteaRepositoryProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
