package org.rsmod.plugins.net.js5

import io.netty.channel.ChannelHandlerContext
import org.rsmod.plugins.net.js5.upstream.Js5Request

private const val MAX_REQUESTS = 200

public class Js5Client(public val ctx: ChannelHandlerContext) {

    private val urgent = ArrayDeque<Js5Request.Group>()

    private val prefetch = ArrayDeque<Js5Request.Group>()

    public fun push(request: Js5Request.Group) {
        if (request.urgent) {
            urgent += request
        } else {
            prefetch += request
        }
    }

    public fun pop(): Js5Request.Group? = urgent.removeFirstOrNull() ?: prefetch.removeFirstOrNull()

    public fun isReady(): Boolean = ctx.channel().isWritable && isNotEmpty()

    public fun isNotEmpty(): Boolean = urgent.isNotEmpty() || prefetch.isNotEmpty()

    public fun isNotFull(): Boolean = urgent.size < MAX_REQUESTS && prefetch.size < MAX_REQUESTS
}
