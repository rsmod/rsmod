package org.rsmod.api.net.rsprot.provider

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.rsprot.protocol.api.js5.Js5Service
import org.openrs2.cache.Js5Compression
import org.openrs2.cache.Js5CompressionType
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store
import org.openrs2.cache.VersionTrailer

class Js5Store(val crc: IntArray, val sizes: Int2IntMap, val responses: Int2ObjectMap<ByteBuf>) {
    fun response(archive: Int, group: Int): ByteBuf =
        responses.getOrDefault((archive shl 16) or group, null)
            ?: error("Response does not exist for $archive:$group.")

    fun size(archive: Int, group: Int): Int =
        sizes.getOrDefault((archive shl 16) or group, null)
            ?: error("Size does not exist for $archive:$group.")

    companion object {
        fun from(store: Store): Js5Store {
            val masterIndex = Js5MasterIndex.create(store)
            val responses = Int2ObjectOpenHashMap<ByteBuf>(1 shl 17 - 1)
            val archives = store.list()
            val sizes = Int2IntOpenHashMap()
            for (archive in archives) {
                val groups = store.list(archive)
                for (group in groups) {
                    val data = store.read(archive, group)
                    if (archive != Store.ARCHIVESET) {
                        VersionTrailer.strip(data)
                    }
                    responses.putResponse(archive, group, data, sizes)
                }
            }
            responses.putResponse(
                Store.ARCHIVESET,
                Store.ARCHIVESET,
                generateMasterResponse(masterIndex),
                sizes,
            )
            val crc = createCrc(masterIndex)
            return Js5Store(crc, sizes, responses)
        }

        private fun createCrc(masterIndex: Js5MasterIndex): IntArray {
            return masterIndex.entries
                .mapTo(mutableListOf(), Js5MasterIndex.Entry::checksum)
                .toIntArray()
        }

        private fun Int2ObjectOpenHashMap<ByteBuf>.putResponse(
            archive: Int,
            group: Int,
            buf: ByteBuf,
            sizes: Int2IntMap,
        ) {
            val readableBytes = buf.readableBytes()
            val output = Unpooled.buffer(readableBytes + 8 + (readableBytes / 512))
            Js5Service.prepareJs5Buffer(archive, group, buf, output)
            val ubuf = Unpooled.unreleasableBuffer(output)
            put((archive shl 16) or group, ubuf)
            sizes[(archive shl 16) or group] = ubuf.writerIndex()
            buf.release(buf.refCnt())
        }

        private fun generateMasterResponse(masterIndex: Js5MasterIndex): ByteBuf {
            val buf = Unpooled.buffer()
            masterIndex.write(buf)
            return Js5Compression.compress(buf, Js5CompressionType.UNCOMPRESSED)
        }
    }
}
