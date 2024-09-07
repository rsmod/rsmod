package org.rsmod.api.net.rsprot

import com.google.inject.Provider
import com.google.inject.Scopes
import com.google.inject.TypeLiteral
import jakarta.inject.Inject
import net.rsprot.protocol.api.NetworkService
import org.rsmod.game.entity.Player
import org.rsmod.plugin.module.PluginModule

class NetworkModule : PluginModule() {
    override fun bind() {
        bind(object : TypeLiteral<NetworkService<Player>>() {})
            .toProvider(NetworkServiceProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}

private class NetworkServiceProvider @Inject constructor(private val factory: NetworkFactory) :
    Provider<NetworkService<Player>> {
    override fun get(): NetworkService<Player> = factory.build()
}
