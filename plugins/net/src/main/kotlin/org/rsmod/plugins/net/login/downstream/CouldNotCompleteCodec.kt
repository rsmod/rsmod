package org.rsmod.plugins.net.login.downstream

import jakarta.inject.Singleton
import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec

@Singleton
public class CouldNotCompleteCodec : ZeroLengthPacketCodec<LoginResponse.CouldNotComplete>(
    packet = LoginResponse.CouldNotComplete,
    opcode = 13
)
