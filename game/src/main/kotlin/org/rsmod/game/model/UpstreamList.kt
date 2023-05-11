package org.rsmod.game.model

import org.rsmod.game.protocol.packet.UpstreamPacket

public class UpstreamList(
    private val packets: MutableList<UpstreamPacket> = mutableListOf()
) : MutableList<UpstreamPacket> by packets
