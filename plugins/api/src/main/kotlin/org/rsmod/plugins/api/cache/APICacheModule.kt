package org.rsmod.plugins.api.cache

import com.google.inject.AbstractModule
import org.rsmod.plugins.api.cache.type.CacheTypeListModule

public object APICacheModule : AbstractModule() {

    override fun configure() {
        install(CacheTypeListModule)
    }
}
