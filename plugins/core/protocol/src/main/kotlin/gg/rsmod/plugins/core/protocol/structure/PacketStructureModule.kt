package gg.rsmod.plugins.core.protocol.structure

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule

class PacketStructureModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<DesktopPacketStructure>()
            .`in`(scope)

        bind<IosPacketStructure>()
            .`in`(scope)

        bind<AndroidPacketStructure>()
            .`in`(scope)
    }
}
