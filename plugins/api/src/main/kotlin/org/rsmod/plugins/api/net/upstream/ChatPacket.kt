package org.rsmod.plugins.api.net.upstream

import org.rsmod.protocol.game.packet.UpstreamPacket

public data class ClientCheat(val text: String) : UpstreamPacket {

    public val args: List<String> = text.split(" ")
}
