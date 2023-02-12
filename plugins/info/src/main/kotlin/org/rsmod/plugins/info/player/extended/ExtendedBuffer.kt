package org.rsmod.plugins.info.player.extended

import org.rsmod.plugins.info.player.extended.ExtendedInfoSizes.TOTAL_BYTE_SIZE

public class ExtendedBuffer(
    public val data: ByteArray = ByteArray(TOTAL_BYTE_SIZE),
    public var offset: Int = 0
) {

    public fun reset(): ExtendedBuffer {
        offset = 0
        return this
    }

    public fun putBytes(src: ByteArray): ExtendedBuffer {
        for (i in src.indices) {
            data[offset + i] = src[i]
        }
        offset += src.size
        return this
    }
}
