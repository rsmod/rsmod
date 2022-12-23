package org.rsmod.plugins.net.service.downstream

import org.rsmod.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class ClientOutOfDateCodec : ZeroLengthPacketCodec<ServiceResponse.ClientOutOfDate>(
    packet = ServiceResponse.ClientOutOfDate,
    opcode = 6
)
