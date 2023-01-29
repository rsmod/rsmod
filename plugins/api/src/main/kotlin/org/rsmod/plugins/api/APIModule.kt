package org.rsmod.plugins.api

import com.google.inject.AbstractModule
import org.rsmod.json.JsonModule
import org.rsmod.plugins.api.cache.map.xtea.XteaModule
import org.rsmod.plugins.api.net.PacketModule

object APIModule : AbstractModule() {

    override fun configure() {
        install(JsonModule)
        install(PacketModule)
        install(XteaModule)
    }
}
