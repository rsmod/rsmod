package org.rsmod.plugins.net.js5.upstream

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

public class Js5RequestDecoder : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() < 4) return
        when (val opcode = input.readUnsignedByte().toInt()) {
            Js5Opcodes.GROUP_PREFETCH, Js5Opcodes.GROUP_URGENT -> {
                val archive = input.readUnsignedByte().toInt()
                val group = input.readUnsignedShort()
                val urgent = opcode == Js5Opcodes.GROUP_URGENT
                out += Js5Request.Group(archive, group, urgent)
            }
            Js5Opcodes.REKEY -> {
                val key = input.readUnsignedByte().toInt()
                input.skipBytes(Short.SIZE_BYTES)
                out += Js5Request.Rekey(key)
            }
            else -> {
                input.skipBytes(3)
                out += when (opcode) {
                    Js5Opcodes.LOGGED_IN -> Js5Request.LoggedIn
                    Js5Opcodes.LOGGED_OUT -> Js5Request.LoggedOut
                    Js5Opcodes.CONNECTED -> Js5Request.Connected
                    Js5Opcodes.DISCONNECT -> Js5Request.Disconnect
                    else -> error("Unhandled JS5 request opcode: $opcode")
                }
            }
        }
    }
}
