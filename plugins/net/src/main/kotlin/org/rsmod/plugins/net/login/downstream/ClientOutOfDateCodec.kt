package org.rsmod.plugins.net.login.downstream

import jakarta.inject.Singleton
import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec

@Singleton
public class ClientOutOfDateCodec : ZeroLengthPacketCodec<LoginResponse.ClientOutOfDate>(
    packet = LoginResponse.ClientOutOfDate,
    opcode = 6
)
