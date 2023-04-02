package org.rsmod.plugins.api

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.buffer.BufferModule
import org.rsmod.game.events.EventBus
import org.rsmod.game.model.WorldClock
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.json.JsonModule
import org.rsmod.plugins.api.cache.CacheModule
import org.rsmod.plugins.api.cache.map.xtea.XteaModule
import org.rsmod.plugins.api.core.GameProcessModule
import org.rsmod.plugins.api.info.InfoModule
import org.rsmod.plugins.api.net.PacketModule
import org.rsmod.plugins.api.net.upstream.handler.UpstreamHandlerModule
import org.rsmod.plugins.api.pathfinder.PathFinderModule
import org.rsmod.toml.TomlModule

public object APIModule : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CacheModule)
        install(GameProcessModule)
        install(InfoModule)
        install(JsonModule)
        install(PacketModule)
        install(PathFinderModule)
        install(TomlModule)
        install(UpstreamHandlerModule)
        install(XteaModule)

        bind(EventBus::class.java).`in`(Scopes.SINGLETON)
        bind(PlayerList::class.java).`in`(Scopes.SINGLETON)
        bind(WorldClock::class.java).`in`(Scopes.SINGLETON)
    }
}
