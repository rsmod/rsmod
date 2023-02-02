package org.rsmod.plugins.net.service.upstream

import org.rsmod.protocol.game.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
public class InitGameConnectionCodec : ZeroLengthPacketCodec<ServiceRequest.InitGameConnection>(
    packet = ServiceRequest.InitGameConnection,
    opcode = 14
)
