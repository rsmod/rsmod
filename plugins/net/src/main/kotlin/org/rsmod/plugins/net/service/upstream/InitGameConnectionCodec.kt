package org.rsmod.plugins.net.service.upstream

import org.rsmod.plugins.net.service.ServiceRequest
import org.rsmod.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class InitGameConnectionCodec : ZeroLengthPacketCodec<ServiceRequest.InitGameConnection>(
    packet = ServiceRequest.InitGameConnection,
    opcode = 14
)
