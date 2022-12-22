package org.rsmod.plugins.net.js5.downstream

import org.rsmod.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class Js5ClientOutOfDateCodec : ZeroLengthPacketCodec<Js5DownstreamResponse>(
    packet = Js5DownstreamResponse.ClientOutOfDate,
    opcode = 6
)
