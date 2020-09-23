package gg.rsmod.plugins.protocol.structure

import com.google.inject.Inject
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
    constructor() : this(
        ServerPacketStructureMap(),
        ClientPacketStructureMap()
    )
}

class IosPacketStructure(
    server: ServerPacketStructureMap,
    client: ClientPacketStructureMap
) : PacketStructureCodec(server, client) {

    @Inject
    constructor() : this(
        ServerPacketStructureMap(),
        ClientPacketStructureMap()
    )
}

class AndroidPacketStructure(
    server: ServerPacketStructureMap,
    client: ClientPacketStructureMap
) : PacketStructureCodec(server, client) {

    @Inject
    constructor() : this(
        ServerPacketStructureMap(),
        ClientPacketStructureMap()
    )
}
