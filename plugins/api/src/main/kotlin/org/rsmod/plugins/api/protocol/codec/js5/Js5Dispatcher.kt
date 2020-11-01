package org.rsmod.plugins.api.protocol.codec.js5

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import org.rsmod.game.cache.GameCache
import io.guthix.js5.container.Js5Store
import io.netty.channel.Channel

private val logger = InlineLogger()

class Js5Dispatcher(
    private val cache: GameCache,
    private val responses: MutableMap<Js5Request, Js5Response>
) {

    @Inject
    constructor(cache: GameCache) : this(cache, mutableMapOf())

    fun cacheResponses() {
        cacheResponse(Js5Store.MASTER_INDEX, Js5Store.MASTER_INDEX)
        for (i in 0 until cache.archiveCount) {
            cacheResponse(Js5Store.MASTER_INDEX, i)
        }
        for (i in 0 until cache.archiveCount) {
            val groups = cache.groups(i)
            groups.forEach { group ->
                cacheResponse(i, group)
            }
        }
        logger.debug { "Cached JS5 request responses (total=${responses.size})" }
    }

    fun add(channel: Channel, request: Js5Request) {
        val response = request.response()
        logger.trace { "Add JS5 request (request=$request, response=$response, channel=$channel)" }
        channel.writeAndFlush(response)
    }

    private fun Js5Request.response(): Js5Response {
        if (responses.isEmpty()) {
            error("::cacheResponses should be called on server startup.")
        }
        val cachedRequest = Js5Request(archive, group, urgent = false)
        return responses[cachedRequest] ?: error("Js5 request was not cached on startup (request=$this)")
    }

    private fun response(archive: Int, group: Int): Js5Response {
        val data = cache.read(archive, group)

        val compressionType = data.readUnsignedByte().toInt()
        val compressedLength = data.readInt()
        val array = ByteArray(data.writerIndex() - Byte.SIZE_BYTES - Int.SIZE_BYTES)
        data.readBytes(array)

        return Js5Response(
            archive = archive,
            group = group,
            compressionType = compressionType,
            compressedLength = compressedLength,
            data = array
        )
    }

    private fun cacheResponse(archive: Int, group: Int) {
        val request = Js5Request(archive, group, urgent = false)
        val response = response(archive, group)
        logger.trace { "Cache Js5 request (request=$request, response=$response)" }
        responses[request] = response
    }
}
