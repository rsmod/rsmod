package org.rsmod.plugins.api.net.upstream.handler

import org.rsmod.protocol.game.packet.UpstreamPacket

public class UpstreamHandlerMap(
    public val handlers: Map<Class<out UpstreamPacket>, UpstreamHandler<*>>
) {

    @Suppress("UNCHECKED_CAST")
    public inline operator fun <reified T : UpstreamPacket> get(type: T): UpstreamHandler<T>? {
        return handlers[type::class.java] as? UpstreamHandler<T>
    }
}
