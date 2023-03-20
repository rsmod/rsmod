package org.rsmod.plugins.api.info

import java.nio.ByteBuffer

internal class ReusableByteBufferMap(playerCapacity: Int, singleBufferCapacity: Int) {

    private val buffers = Array(playerCapacity) { ByteBuffer.allocate(singleBufferCapacity) }

    operator fun get(playerIndex: Int): ByteBuffer {
        return buffers[playerIndex]
    }
}
