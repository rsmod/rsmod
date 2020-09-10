package gg.rsmod.plugins.protocol.structure

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.plugins.protocol.DesktopPacketStructure

class PacketStructureModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<DesktopPacketStructure>()
            .`in`(scope)
    }
}
