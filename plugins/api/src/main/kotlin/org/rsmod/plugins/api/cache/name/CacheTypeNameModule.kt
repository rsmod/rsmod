package org.rsmod.plugins.api.cache.name

import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object CacheTypeNameModule : AbstractModule() {

    override fun configure() {
        bind(CacheTypeNameLoader::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
