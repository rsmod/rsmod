package org.rsmod.plugins.net.js5

import io.netty.channel.ChannelHandlerContext
import org.rsmod.plugins.net.js5.upstream.Js5Request

private const val MAX_URGENT_REQUESTS = 250
private const val MAX_PREFETCH_REQUESTS = 50

class Js5Client(val ctx: ChannelHandlerContext) {

    private val urgent = ArrayDeque<Js5Request.Group>()

    private val prefetch = ArrayDeque<Js5Request.Group>()

    fun push(request: Js5Request.Group) {
        if (request.urgent) {
            urgent += request
        } else {
            prefetch += request
        }
    }

    fun pop(): Js5Request.Group? = urgent.removeFirstOrNull() ?: prefetch.removeFirstOrNull()

    fun isReady(): Boolean = ctx.channel().isWritable && isNotEmpty()

    fun isNotEmpty(): Boolean = urgent.isNotEmpty() || prefetch.isNotEmpty()

    fun isNotFull(): Boolean = urgent.size < MAX_URGENT_REQUESTS || prefetch.size < MAX_PREFETCH_REQUESTS
}
