package org.rsmod.plugins.net.login.downstream

import org.rsmod.protocol.game.packet.ZeroLengthPacketCodec
import javax.inject.Singleton

@Singleton
public class WorldIsFullCodec : ZeroLengthPacketCodec<LoginResponse.WorldIsFull>(
    packet = LoginResponse.WorldIsFull,
    opcode = 7
)
