package org.rsmod.plugins.net.login.downstream

import org.rsmod.protocol.game.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
public class ClientProtocolOutOfDateCodec : ZeroLengthPacketCodec<LoginResponse.ClientProtocolOutOfDate>(
    packet = LoginResponse.ClientProtocolOutOfDate,
    opcode = 68
)
