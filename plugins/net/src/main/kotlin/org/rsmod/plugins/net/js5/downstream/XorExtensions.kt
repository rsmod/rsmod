package org.rsmod.plugins.net.js5.downstream

import io.netty.buffer.ByteBuf

internal fun ByteBuf.xor(key: Int): ByteBuf {
    if (key == 0) return retain()
    val buf = if (refCnt() == 1) {
        retain()
    } else {
        copy()
    }
    if (buf.hasArray()) {
        val array = buf.array()
        val off = buf.arrayOffset() + buf.readerIndex()
        val len = buf.readableBytes()
        for (i in off until off + len) {
            array[i] = (array[i].toInt() xor key).toByte()
        }
    } else {
        val off = buf.readerIndex()
        val len = buf.readableBytes()
        for (i in off until off + len) {
            buf.setByte(i, buf.getByte(i).toInt() xor key)
        }
    }
    return buf
}
