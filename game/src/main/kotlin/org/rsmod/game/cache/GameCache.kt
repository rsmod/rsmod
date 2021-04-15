package org.rsmod.game.cache

import io.guthix.js5.Js5Archive
import io.guthix.js5.Js5Cache
import io.guthix.js5.Js5File
import io.guthix.js5.Js5Group
import io.guthix.js5.container.Js5Container
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.js5.util.XTEA_ZERO_KEY
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.file.Path

class GameCache(
    val directory: Path,
    private val store: Js5DiskStore,
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

    fun putFile(group: Js5Group, fileId: Int, buf: ByteBuf, nameHash: Int? = null) {
        val file = Js5File(fileId, nameHash, buf)
        group.files[fileId] = file
    }

    fun packGroup(archive: Js5Archive, group: Js5Group) {
        archive.writeGroup(group, appendVersion = true)
    }

    fun packArchive(archive: Js5Archive) {
        cache.writeArchive(archive)
    }

    fun close() {
        store.close()
        cache.close()
    }

    fun archive(archive: Int): Js5Archive {
        return cache.readArchive(archive)
    }

    fun group(archive: Js5Archive, groupId: Int, xtea: IntArray = XTEA_ZERO_KEY): Js5Group {
        return archive.readGroup(groupId, xtea)
    }

    fun groups(archive: Int, group: Int): Map<Int, ByteBuf> {
        return cache.readArchive(archive).readGroup(group).files.mapValues { it.value.data.retain() }
    }

    fun file(archive: Js5Archive, group: String, file: Int, xtea: IntArray = XTEA_ZERO_KEY): ByteBuf {
        return archive.readGroup(group, xtea).files.getValue(file).data.retain()
    }

    fun file(group: Js5Group, file: Int): ByteBuf {
        return group.files[file]?.data ?: Unpooled.EMPTY_BUFFER
    }

    fun read(archive: Int, group: Int): ByteBuf = store.read(archive, group).retain()

    fun groupIds(archive: Int): List<Int> = cache.readArchive(archive).groupSettings.map { it.key }
}
