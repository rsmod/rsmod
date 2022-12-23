package org.rsmod.plugins.net.js5.downstream

import org.rsmod.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class Js5ClientOutOfDateCodec : ZeroLengthPacketCodec<Js5Response>(
    packet = Js5Response.ClientOutOfDate,
    opcode = 6
)
