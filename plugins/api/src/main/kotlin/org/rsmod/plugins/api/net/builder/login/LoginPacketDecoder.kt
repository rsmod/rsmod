package org.rsmod.plugins.api.net.builder.login

import io.netty.buffer.ByteBuf

data class LoginPacketDecoder<T : LoginPacket>(val decode: (ByteBuf) -> T)
