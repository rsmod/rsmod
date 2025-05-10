package org.rsmod.api.cache.util

@JvmInline
public value class InlineByteBuf(public val backing: ByteArray) {
    public fun readByte(cursor: Cursor): Cursor {
        val value = backing[cursor.pos].toInt()
        return Cursor(value, cursor.pos + Byte.SIZE_BYTES)
    }

    public fun readUnsignedByte(cursor: Cursor): Cursor {
        val value = backing[cursor.pos].toInt()
        return Cursor(value and 0xFF, cursor.pos + Byte.SIZE_BYTES)
    }

    public fun readShort(cursor: Cursor): Cursor {
        val value = backing[cursor.pos].toInt() shl 8 or (backing[cursor.pos + 1].toInt() and 0xFF)
        return Cursor(value, cursor.pos + Short.SIZE_BYTES)
    }

    public fun readShortSmart(cursor: Cursor): Cursor =
        if ((backing[cursor.pos].toInt() and 0x80) == 0) {
            readByte(cursor)
        } else {
            readShort(cursor)
        }

    public fun readIncrShortSmart(cursor: Cursor): Cursor {
        var value = 0
        var curr = readShortSmart(cursor)
        while ((curr.value and 0x7FFF) == 0x7FFF) {
            value += curr.value and 0x7FFF
            curr = readShortSmart(curr)
        }
        value += curr.value and 0x7FFF
        return Cursor(value, curr.pos)
    }

    public fun readInt(cursor: Cursor): Cursor {
        val byte1 = (backing[cursor.pos].toInt() and 0xFF) shl 24
        val byte2 = (backing[cursor.pos + 1].toInt() and 0xFF) shl 16
        val byte3 = (backing[cursor.pos + 2].toInt() and 0xFF) shl 8
        val byte4 = (backing[cursor.pos + 3].toInt() and 0xFF)
        return Cursor(byte1 or byte2 or byte3 or byte4, cursor.pos + Int.SIZE_BYTES)
    }

    public fun isReadable(cursor: Cursor): Boolean = cursor.pos < backing.size

    public fun newCursor(): Cursor = Cursor(0)

    @JvmInline
    public value class Cursor(public val packed: Long) {
        public val value: Int
            get() = (packed shr VALUE_BIT_OFFSET).toInt()

        public val pos: Int
            get() = ((packed shr POS_BIT_OFFSET) and POS_BIT_MASK).toInt()

        public constructor(value: Int, pos: Int) : this(pack(value, pos))

        override fun toString(): String = "Cursor(value=$value, pos=$pos)"

        public companion object {
            public const val VALUE_BIT_COUNT: Int = 32
            public const val POS_BIT_COUNT: Int = 32

            public const val VALUE_BIT_OFFSET: Int = 0
            public const val POS_BIT_OFFSET: Int = VALUE_BIT_OFFSET + VALUE_BIT_COUNT

            public const val VALUE_BIT_MASK: Long = (1L shl VALUE_BIT_COUNT) - 1
            public const val POS_BIT_MASK: Long = (1L shl POS_BIT_COUNT) - 1

            private fun pack(value: Int, pos: Int): Long =
                ((value.toLong() and VALUE_BIT_MASK) shl VALUE_BIT_OFFSET) or
                    ((pos.toLong() and POS_BIT_MASK) shl POS_BIT_OFFSET)
        }
    }
}
