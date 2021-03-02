package org.rsmod.game.net

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.net.handshake.HandshakeHandlerMap

class NetworkModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<HandshakeHandlerMap>()
            .`in`(scope)
    }
}
