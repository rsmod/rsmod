package org.rsmod.plugins.net.login.downstream

import org.rsmod.protocol.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
class ClientProtocolOutOfDateCodec : ZeroLengthPacketCodec<LoginResponse.ClientProtocolOutOfDate>(
    packet = LoginResponse.ClientProtocolOutOfDate,
    opcode = 68
)
