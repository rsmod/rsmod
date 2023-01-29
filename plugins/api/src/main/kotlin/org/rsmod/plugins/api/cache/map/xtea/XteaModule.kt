package org.rsmod.plugins.api.cache.map.xtea

import com.google.inject.AbstractModule
import com.google.inject.Scopes

object XteaModule : AbstractModule() {

    override fun configure() {
        bind(XteaRepository::class.java).`in`(Scopes.SINGLETON)
    }
}
