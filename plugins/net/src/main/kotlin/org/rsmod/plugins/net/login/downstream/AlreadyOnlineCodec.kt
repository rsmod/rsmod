package org.rsmod.plugins.net.login.downstream

import jakarta.inject.Singleton
import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec

@Singleton
public class AlreadyOnlineCodec : ZeroLengthPacketCodec<LoginResponse.AlreadyOnline>(
    packet = LoginResponse.AlreadyOnline,
    opcode = 5
)
