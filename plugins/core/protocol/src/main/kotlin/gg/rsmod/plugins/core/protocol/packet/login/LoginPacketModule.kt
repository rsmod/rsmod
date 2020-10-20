package gg.rsmod.plugins.core.protocol.packet.login

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule

class LoginPacketModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<LoginPacketMap>()
            .`in`(scope)
    }
}
