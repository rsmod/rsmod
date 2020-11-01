package org.rsmod.plugins.api.protocol.structure

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule

class PacketStructureModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<DevicePacketStructureMap>()
            .`in`(scope)
    }
}
