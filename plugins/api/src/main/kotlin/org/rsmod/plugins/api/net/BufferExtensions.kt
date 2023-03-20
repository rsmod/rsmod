@file:Suppress("NOTHING_TO_INLINE")

package org.rsmod.plugins.api.net

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readByteA
import org.openrs2.buffer.readByteC
import org.openrs2.buffer.readByteS
import org.openrs2.buffer.readIntAlt3
import org.openrs2.buffer.readIntAlt3Reverse
import org.openrs2.buffer.readShortA
import org.openrs2.buffer.readShortLEA
import org.openrs2.buffer.readUnsignedByteA
import org.openrs2.buffer.readUnsignedByteC
import org.openrs2.buffer.readUnsignedByteS
import org.openrs2.buffer.readUnsignedShortA
import org.openrs2.buffer.readUnsignedShortLEA
import org.openrs2.buffer.readUnsignedShortSmart
import org.openrs2.buffer.writeByteA
import org.openrs2.buffer.writeByteC
import org.openrs2.buffer.writeByteS
import org.openrs2.buffer.writeIntAlt3
import org.openrs2.buffer.writeIntAlt3Reverse
import org.openrs2.buffer.writeShortA
import org.openrs2.buffer.writeShortLEA

public inline fun ByteBuf.writeByteAlt1(value: Int): ByteBuf = writeByteA(value)
public inline fun ByteBuf.readByteAlt1(): Byte = readByteA()
public inline fun ByteBuf.readUnsignedByteAlt1(): Short = readUnsignedByteA()

public inline fun ByteBuf.writeByteAlt2(value: Int): ByteBuf = writeByteC(value)
public inline fun ByteBuf.readByteAlt2(): Byte = readByteC()
public inline fun ByteBuf.readUnsignedByteAlt2(): Short = readUnsignedByteC()

public inline fun ByteBuf.writeByteAlt3(value: Int): ByteBuf = writeByteS(value)
public inline fun ByteBuf.readByteAlt3(): Byte = readByteS()
public inline fun ByteBuf.readUnsignedByteAlt3(): Short = readUnsignedByteS()

public inline fun ByteBuf.writeShortAlt1(value: Int): ByteBuf = writeShortLE(value)
public inline fun ByteBuf.readShortAlt1(): Short = readShortLE()
public inline fun ByteBuf.readUnsignedShortAlt1(): Int = readUnsignedShortLE()

public inline fun ByteBuf.writeShortAlt2(value: Int): ByteBuf = writeShortA(value)
public inline fun ByteBuf.readShortAlt2(): Short = readShortA()
public inline fun ByteBuf.readUnsignedShortAlt2(): Int = readUnsignedShortA()

public inline fun ByteBuf.writeShortAlt3(value: Int): ByteBuf = writeShortLEA(value)
public inline fun ByteBuf.readShortAlt3(): Short = readShortLEA()
public inline fun ByteBuf.readUnsignedShortAlt3(): Int = readUnsignedShortLEA()

public inline fun ByteBuf.writeMediumAlt1(value: Int): ByteBuf = writeMediumLE(value)
public inline fun ByteBuf.readMediumAlt1(): Int = readMediumLE()
public inline fun ByteBuf.readUnsignedMediumAlt1(): Int = readUnsignedMediumLE()

public inline fun ByteBuf.writeIntAlt1(value: Int): ByteBuf = writeIntLE(value)
public inline fun ByteBuf.readIntAlt1(): Int = readIntLE()

public inline fun ByteBuf.writeIntAlt2(value: Int): ByteBuf = writeIntAlt3Reverse(value)
public inline fun ByteBuf.readIntAlt2(): Int = readIntAlt3Reverse()

@Suppress("FunctionName")
public inline fun ByteBuf.writeIntAlt3_(value: Int): ByteBuf = writeIntAlt3(`value` = value)

@Suppress("FunctionName")
public inline fun ByteBuf.readIntAlt3_(): Int = readIntAlt3()

public fun ByteBuf.readIncrUnsignedShortSmart(): Int {
    var value = 0
    var curr = readUnsignedShortSmart()
    while (curr == 0x7FFF) {
        value += curr
        curr = readUnsignedShortSmart()
    }
    value += curr
    return value
}
