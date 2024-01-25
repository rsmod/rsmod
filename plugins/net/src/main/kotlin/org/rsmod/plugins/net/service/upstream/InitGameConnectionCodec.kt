package org.rsmod.plugins.net.service.upstream

import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec
import jakarta.inject.Singleton

@Singleton
public class InitGameConnectionCodec : ZeroLengthPacketCodec<ServiceRequest.InitGameConnection>(
    packet = ServiceRequest.InitGameConnection,
    opcode = 14
)
