package org.rsmod.plugins.info.player.buffer

public class SimpleBuffer(
    public val data: ByteArray,
    public var offset: Int = 0
) {

    public val capacity: Int get() = data.size

    public constructor(capacity: Int) : this(ByteArray(capacity))

    public fun clear(): SimpleBuffer {
        offset = 0
        return this
    }

    public fun putBytes(src: ByteArray, length: Int): SimpleBuffer {
        for (i in 0 until length) {
            data[offset + i] = src[i]
        }
        offset += length
        return this
    }

    override fun toString(): String {
        return "SimpleBuffer(offset=$offset, capacity=$capacity)"
    }
}
