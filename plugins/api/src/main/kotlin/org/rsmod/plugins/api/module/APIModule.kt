package org.rsmod.plugins.api.module

import com.google.inject.AbstractModule
import org.rsmod.json.JsonModule
import org.rsmod.plugins.api.cache.APICacheModule
import org.rsmod.plugins.api.cache.map.xtea.XteaModule
import org.rsmod.plugins.api.info.InfoModule
import org.rsmod.plugins.api.net.PacketModule
import org.rsmod.plugins.api.net.upstream.handler.UpstreamHandlerModule

public object APIModule : AbstractModule() {

    override fun configure() {
        install(APICacheModule)
        install(InfoModule)
        install(JsonModule)
        install(PacketModule)
        install(UpstreamHandlerModule)
        install(XteaModule)
    }
}
