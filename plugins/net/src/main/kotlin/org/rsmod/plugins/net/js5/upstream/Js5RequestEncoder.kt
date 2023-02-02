package org.rsmod.plugins.net.js5.upstream

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

@Suppress("UNUSED")
@Sharable
public object Js5RequestEncoder : MessageToByteEncoder<Js5Request>(Js5Request::class.java) {

    override fun encode(ctx: ChannelHandlerContext, msg: Js5Request, out: ByteBuf) {
        when (msg) {
            is Js5Request.Group -> {
                val opcode = if (msg.urgent) Js5Opcodes.GROUP_URGENT else Js5Opcodes.GROUP_PREFETCH
                out.writeByte(opcode)
                out.writeByte(msg.archive)
                out.writeShort(msg.group)
            }
            Js5Request.LoggedIn -> {
                out.writeByte(Js5Opcodes.LOGGED_IN)
                out.writeZero(3)
            }
            Js5Request.LoggedOut -> {
                out.writeByte(Js5Opcodes.LOGGED_OUT)
                out.writeZero(3)
            }
            is Js5Request.Rekey -> {
                out.writeByte(Js5Opcodes.REKEY)
                out.writeByte(msg.key)
                out.writeZero(2)
            }
            Js5Request.Connected -> {
                out.writeByte(Js5Opcodes.CONNECTED)
                out.writeMedium(3)
            }
            Js5Request.Disconnect -> {
                out.writeByte(Js5Opcodes.DISCONNECT)
                out.writeZero(3)
            }
        }
    }

    override fun allocateBuffer(ctx: ChannelHandlerContext, msg: Js5Request, preferDirect: Boolean): ByteBuf {
        return if (preferDirect) {
            ctx.alloc().ioBuffer(4, 4)
        } else {
            ctx.alloc().heapBuffer(4, 4)
        }
    }
}
