package org.rsmod.plugins.net.js5.downstream

import org.rsmod.protocol.packet.Packet

sealed class Js5DownstreamResponse : Packet {
    object Ok : Js5DownstreamResponse()
    object ClientOutOfDate : Js5DownstreamResponse()
}
