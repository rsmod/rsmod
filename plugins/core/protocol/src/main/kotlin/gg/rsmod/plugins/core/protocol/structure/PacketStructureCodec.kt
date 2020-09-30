package gg.rsmod.plugins.core.protocol.structure

import com.google.inject.Inject
import com.google.inject.Injector
import gg.rsmod.game.message.ClientPacketStructureMap
import gg.rsmod.game.message.ServerPacketStructureMap

sealed class PacketStructureCodec(
    val server: ServerPacketStructureMap,
    val client: ClientPacketStructureMap
)

class DesktopPacketStructure(
    server: ServerPacketStructureMap,
    client: ClientPacketStructureMap
) : PacketStructureCodec(server, client) {

    @Inject
    constructor(injector: Injector) : this(
        ServerPacketStructureMap(),
        ClientPacketStructureMap(injector)
    )
}

class IosPacketStructure(
    server: ServerPacketStructureMap,
    client: ClientPacketStructureMap
) : PacketStructureCodec(server, client) {

    @Inject
    constructor(injector: Injector) : this(
        ServerPacketStructureMap(),
        ClientPacketStructureMap(injector)
    )
}

class AndroidPacketStructure(
    server: ServerPacketStructureMap,
    client: ClientPacketStructureMap
) : PacketStructureCodec(server, client) {

    @Inject
    constructor(injector: Injector) : this(
        ServerPacketStructureMap(),
        ClientPacketStructureMap(injector)
    )
}
