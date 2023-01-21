package org.rsmod.plugins.net.login.downstream

import org.rsmod.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class BadSessionIdCodec : ZeroLengthPacketCodec<LoginResponse.BadSessionId>(
    packet = LoginResponse.BadSessionId,
    opcode = 10
)
