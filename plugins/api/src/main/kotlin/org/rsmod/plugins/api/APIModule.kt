package org.rsmod.plugins.api

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.json.JsonModule
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository

object APIModule : AbstractModule() {

    override fun configure() {
        install(JsonModule)

        bind(XteaRepository::class.java).`in`(Scopes.SINGLETON)
    }
}
