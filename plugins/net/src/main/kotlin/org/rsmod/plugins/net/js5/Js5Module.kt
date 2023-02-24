package org.rsmod.plugins.net.js5

import com.google.inject.AbstractModule
import org.rsmod.plugins.net.js5.cache.Js5CacheModule

public object Js5Module : AbstractModule() {

    override fun configure() {
        install(Js5CacheModule)
    }
}
