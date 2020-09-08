package gg.rsmod.plugins.protocol

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule

class PacketStructureModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<DesktopPacketStructure>()
            .`in`(scope)
    }
}
