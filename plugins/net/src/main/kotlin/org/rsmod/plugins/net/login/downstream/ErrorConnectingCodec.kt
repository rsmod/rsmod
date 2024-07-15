package org.rsmod.plugins.net.login.downstream

import jakarta.inject.Singleton
import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec

@Singleton
public class ErrorConnectingCodec : ZeroLengthPacketCodec<LoginResponse.ErrorConnecting>(
    packet = LoginResponse.ErrorConnecting,
    opcode = -2
)
