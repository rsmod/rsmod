package org.rsmod.plugins.net.login.downstream

import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec
import com.google.inject.Singleton

@Singleton
public class CouldNotCompleteCodec : ZeroLengthPacketCodec<LoginResponse.CouldNotComplete>(
    packet = LoginResponse.CouldNotComplete,
    opcode = 13
)
