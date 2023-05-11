package org.rsmod.plugins.net.js5.downstream

import org.rsmod.game.protocol.packet.Packet

public sealed class Js5Response : Packet {
    public object Ok : Js5Response()
    public object ClientOutOfDate : Js5Response()
}
