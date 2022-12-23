package org.rsmod.plugins.net.service.downstream

import org.rsmod.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class ClientProtocolOutOfDateCodec : ZeroLengthPacketCodec<ServiceResponse.ClientProtocolOutOfDate>(
    packet = ServiceResponse.ClientProtocolOutOfDate,
    opcode = 68
)
