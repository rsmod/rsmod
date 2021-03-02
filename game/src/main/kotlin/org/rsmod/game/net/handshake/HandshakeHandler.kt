package org.rsmod.game.net.handshake

import com.github.michaelbull.logging.InlineLogger
import com.google.common.base.MoreObjects
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder
import org.rsmod.game.net.channel.ClientChannelHandler

private val logger = InlineLogger()
private const val UNINITIALISED_OPCODE = -1

data class HandshakeHandler(
    val decoder: ClientChannelHandler<ByteToMessageDecoder>,
    val encoder: ClientChannelHandler<MessageToByteEncoder<*>>,
    val adapter: ClientChannelHandler<ChannelInboundHandlerAdapter>,
    val response: ClientChannelHandler<MessageToByteEncoder<*>>
) {

    override fun toString(): String = MoreObjects
        .toStringHelper(this)
        .add("decoder", decoder.name)
        .add("encoder", encoder.name)
        .add("adapter", adapter.name)
        .add("response", response.name)
        .toString()
}

data class HandshakeHandlerMap(
    private val handlers: MutableMap<Int, HandshakeHandler> = mutableMapOf()
) : Map<Int, HandshakeHandler> by handlers {

    fun register(init: HandshakeHandlerBuilder.() -> Unit) {
        val builder = HandshakeHandlerBuilder().apply(init)
        val handler = builder.build()
        val opcode = builder.opcode
        if (handlers.containsKey(opcode)) {
            error("Handshake with opcode already defined (opcode=$opcode).")
        }
        logger.debug { "Register handshake handler (opcode=$opcode)" }
        handlers[opcode] = handler
    }
}

@DslMarker
private annotation class HandshakeHandlerDsl

@HandshakeHandlerDsl
class HandshakeHandlerBuilder(
    var opcode: Int = UNINITIALISED_OPCODE,
    private var decoderHandler: ClientChannelHandler<ByteToMessageDecoder>? = null,
    private var encoderHandler: ClientChannelHandler<MessageToByteEncoder<*>>? = null,
    private var adapterHandler: ClientChannelHandler<ChannelInboundHandlerAdapter>? = null,
    private var responseHandler: ClientChannelHandler<MessageToByteEncoder<*>>? = null
) {

    fun decoder(init: HandshakeClientHandlerBuilder<ByteToMessageDecoder>.() -> Unit) {
        val builder = HandshakeClientHandlerBuilder<ByteToMessageDecoder>().apply(init)
        val decoder = builder.build()
        this.decoderHandler = decoder
    }

    fun encoder(init: HandshakeClientHandlerBuilder<MessageToByteEncoder<*>>.() -> Unit) {
        val builder = HandshakeClientHandlerBuilder<MessageToByteEncoder<*>>().apply(init)
        val encoder = builder.build()
        this.encoderHandler = encoder
    }

    fun adapter(init: HandshakeClientHandlerBuilder<ChannelInboundHandlerAdapter>.() -> Unit) {
        val builder = HandshakeClientHandlerBuilder<ChannelInboundHandlerAdapter>().apply(init)
        val adapter = builder.build()
        this.adapterHandler = adapter
    }

    fun response(init: HandshakeClientHandlerBuilder<MessageToByteEncoder<*>>.() -> Unit) {
        val builder = HandshakeClientHandlerBuilder<MessageToByteEncoder<*>>().apply(init)
        val response = builder.build()
        this.responseHandler = response
    }

    internal fun build(): HandshakeHandler {
        if (opcode == UNINITIALISED_OPCODE) {
            error("Handshake opcode has not been set.")
        }
        val decoder = this.decoderHandler ?: error("Handshake decoder has not been set.")
        val encoder = this.encoderHandler ?: error("Handshake encoder has not been set.")
        val adapter = this.adapterHandler ?: error("Handshake adapter has not been set.")
        val response = this.responseHandler ?: error("Handshake response has not been set.")
        return HandshakeHandler(decoder, encoder, adapter, response)
    }
}

@HandshakeHandlerDsl
class HandshakeClientHandlerBuilder<T : ChannelHandler>(
    var provider: (() -> T)? = null,
    var name: String? = null
) {

    internal fun build(): ClientChannelHandler<T> {
        val handler = this.provider ?: error("Handshake handler provider has not been set.")
        val name = this.name ?: error("Handshake handler name has not been set.")
        return ClientChannelHandler(handler, name)
    }
}
