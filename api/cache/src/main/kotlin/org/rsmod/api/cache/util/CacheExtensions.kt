package org.rsmod.api.cache.util

import io.netty.buffer.ByteBuf
import java.io.FileNotFoundException
import org.openrs2.cache.Cache
import org.openrs2.crypto.SymmetricKey

public fun Cache.readOrNull(archive: Int, group: Int, file: Int): ByteBuf? =
    try {
        read(archive, group, file)
    } catch (_: FileNotFoundException) {
        null
    }

public fun Cache.readOrNull(
    archive: Int,
    group: String,
    file: Int,
    key: SymmetricKey = SymmetricKey.ZERO,
): ByteBuf? =
    try {
        read(archive, group, file, key)
    } catch (_: FileNotFoundException) {
        null
    }
