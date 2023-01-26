package org.rsmod.plugins.api.prot.builder.login

import io.netty.buffer.ByteBuf

data class LoginPacketDecoder<T : LoginPacket>(val decode: (ByteBuf) -> T)
