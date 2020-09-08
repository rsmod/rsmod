package gg.rsmod.plugins.protocol.codec.js5

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.cache.GameCache
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import net.runelite.cache.fs.Container
import net.runelite.cache.fs.jagex.CompressionType
import net.runelite.cache.fs.jagex.DiskStorage

private val logger = InlineLogger()

class Js5Dispatcher @Inject constructor(
    private val cache: GameCache
) {
    /*
     * TODO:
     *  - cache response data for every valid request since OSRS cache is relatively small
     *  - replace runelite cache lib
     */

    fun add(channel: Channel, request: Js5Request) {
        val response = request.response()
        logger.trace { "Add JS5 request (request=$request, response=$response, channel=$channel)" }
        channel.writeAndFlush(response)
    }

    fun combinedArchiveIndexData(archive: Int, group: Int): Js5Response {
        val buf = Unpooled.buffer(cache.archiveCount * (Int.SIZE_BYTES * 2))

        cache.store.indexes.forEach { index ->
            buf.writeInt(index.crc)
            buf.writeInt(index.revision)
        }

        val container = Container(CompressionType.NONE, -1)
        container.compress(buf.array().copyOf(buf.readableBytes()), null)
        buf.release()

        val data = container.data
        return Js5Response(
            archive = archive,
            group = group,
            data = data
        )
    }

    fun groupIndexData(archive: Int, group: Int): Js5Response {
        val storage = cache.store.storage as DiskStorage
        val data = storage.readIndex(group)
        return Js5Response(
            archive = archive,
            group = group,
            data = data
        )
    }

    fun groupData(archive: Int, group: Int): Js5Response {
        val cacheArchive = cache.store.findIndex(archive)
        val cacheGroup = cacheArchive.getArchive(group)

        val data = cache.store.storage.loadArchive(cacheGroup)
        return Js5Response(
            archive = archive,
            group = group,
            data = data
        )
    }

    private fun Js5Request.response(): Js5Response = when {
        archive == 255 && group == 255 -> combinedArchiveIndexData(archive, group)
        archive == 255 -> groupIndexData(archive, group)
        else -> groupData(archive, group)
    }
}
