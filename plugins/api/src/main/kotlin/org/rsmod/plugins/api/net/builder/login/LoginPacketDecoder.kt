package org.rsmod.plugins.api.net.builder.login

import io.netty.buffer.ByteBuf

public data class LoginPacketDecoder<T : LoginPacket>(val decode: (ByteBuf) -> T)
