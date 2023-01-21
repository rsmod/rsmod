package org.rsmod.plugins.net.rev.builder.login

import io.netty.buffer.ByteBuf

data class LoginPacketDecoder<T : LoginPacket>(val decode: (ByteBuf) -> T)
