package gg.rsmod.game.cache

import io.guthix.js5.Js5Cache
import io.guthix.js5.container.Js5Container
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.heap.Js5HeapStore
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

    fun read(archive: Int, group: Int): ByteBuf = store.read(archive, group).retain()

    fun readGroups(archive: Int, group: Int): Map<Int, ByteBuf> {
        return cache.readArchive(archive).readGroup(group).files.mapValues { it.value.data.retain() }
    }

    fun groups(archive: Int): List<Int> = cache.readArchive(archive).groupSettings.map { it.key }
}
