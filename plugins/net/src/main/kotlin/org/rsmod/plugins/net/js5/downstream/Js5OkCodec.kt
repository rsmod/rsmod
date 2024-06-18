package org.rsmod.plugins.net.js5.downstream

import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec
import com.google.inject.Singleton

@Singleton
public class Js5OkCodec : ZeroLengthPacketCodec<Js5Response>(
    packet = Js5Response.Ok,
    opcode = 0
)
