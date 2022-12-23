package org.rsmod.plugins.net.service.downstream

import org.rsmod.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class BadSessionIdCodec : ZeroLengthPacketCodec<ServiceResponse.BadSessionId>(
    packet = ServiceResponse.BadSessionId,
    opcode = 10
)
