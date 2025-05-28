package org.rsmod.api.cache.util

import io.netty.buffer.ByteBuf
import java.io.FileNotFoundException
import java.util.Collections
import org.openrs2.cache.Cache
import org.openrs2.cache.Js5Index
import org.openrs2.crypto.SymmetricKey

public fun Cache.readOrNull(archive: Int, group: Int, file: Int = 0): ByteBuf? =
    try {
        read(archive, group, file)
    } catch (_: FileNotFoundException) {
        null
    }

public fun Cache.readOrNull(
    archive: Int,
    group: String,
    file: Int = 0,
    key: SymmetricKey = SymmetricKey.ZERO,
): ByteBuf? =
    try {
        read(archive, group, file, key)
    } catch (_: FileNotFoundException) {
        null
    }

public fun Cache.listOrEmpty(archive: Int, group: Int): Iterator<Js5Index.File> =
    try {
        list(archive, group)
    } catch (_: FileNotFoundException) {
        Collections.emptyIterator()
    }
