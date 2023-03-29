package org.rsmod.plugins.info.buffer

public fun BitBuffer.isCapped(extendedByteLength: Int, safetyBuffer: Int): Boolean {
    val bytePos = (position() + 7) / Byte.SIZE_BITS
    val capacity = (capacity() / Byte.SIZE_BITS)
    return bytePos + extendedByteLength + safetyBuffer >= capacity
}
