package org.rsmod.game.cache

import io.guthix.js5.Js5Archive
import io.guthix.js5.Js5Cache
import io.guthix.js5.container.Js5Container
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.heap.Js5HeapStore
import io.guthix.js5.util.XTEA_ZERO_KEY
import io.netty.buffer.ByteBuf

class GameCache(
    private val store: Js5HeapStore,
    private val cache: Js5Cache,
    private val crcs: MutableList<Int> = mutableListOf()
) {

    val archiveCount: Int
        get() = cache.archiveCount

    val archiveCrcs: IntArray
        get() = crcs.toIntArray()

    fun start() {
        val validator = cache.generateValidator(
            includeWhirlpool = false,
            includeSizes = false
        )
        val container = Js5Container(validator.encode())
        store.write(
            indexId = Js5Store.MASTER_INDEX,
            containerId = Js5Store.MASTER_INDEX,
            data = container.encode()
        )

        val archiveCrcs = validator.archiveValidators.map { it.crc }
        crcs.addAll(archiveCrcs)
    }

    fun archive(archive: Int): Js5Archive {
        return cache.readArchive(archive)
    }

    fun groups(archive: Int, group: Int): Map<Int, ByteBuf> {
        return cache.readArchive(archive).readGroup(group).files.mapValues { it.value.data.retain() }
    }

    fun singleFile(archive: Js5Archive, group: String, xtea: IntArray = XTEA_ZERO_KEY): ByteBuf {
        return archive.readGroup(group, xtea).files.values.first().data
    }

    fun read(archive: Int, group: Int): ByteBuf = store.read(archive, group).retain()

    fun groupIds(archive: Int): List<Int> = cache.readArchive(archive).groupSettings.map { it.key }
}
