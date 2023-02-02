package org.rsmod.plugins.net.js5.downstream

import org.rsmod.protocol.game.packet.Packet

public sealed class Js5Response : Packet {
    public object Ok : Js5Response()
    public object ClientOutOfDate : Js5Response()
}
