package org.rsmod.plugins.net.login.downstream

import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
public class AlreadyOnlineCodec : ZeroLengthPacketCodec<LoginResponse.AlreadyOnline>(
    packet = LoginResponse.AlreadyOnline,
    opcode = 5
)
