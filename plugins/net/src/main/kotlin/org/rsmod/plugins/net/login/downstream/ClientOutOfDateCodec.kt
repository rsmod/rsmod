package org.rsmod.plugins.net.login.downstream

import org.rsmod.protocol.game.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class ClientOutOfDateCodec : ZeroLengthPacketCodec<LoginResponse.ClientOutOfDate>(
    packet = LoginResponse.ClientOutOfDate,
    opcode = 6
)
