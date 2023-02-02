package org.rsmod.plugins.net.js5.downstream

import org.rsmod.protocol.game.packet.Packet

sealed class Js5Response : Packet {
    object Ok : Js5Response()
    object ClientOutOfDate : Js5Response()
}
