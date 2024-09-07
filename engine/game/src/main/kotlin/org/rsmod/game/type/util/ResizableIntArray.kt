package org.rsmod.game.type.util

public class ResizableIntArray(private val entries: Array<Int?>) : Iterable<Int?> {
    public constructor(intArray: IntArray) : this(intArray.map { it }.toTypedArray())

    public constructor(capacity: Int = DEFAULT_CAPACITY) : this(arrayOfNulls(capacity))

    public constructor(
        first: Int,
        vararg entries: Int,
    ) : this(arrayOf(first, *entries.toTypedArray()))

    public val capacity: Int
        get() = entries.size

    public operator fun get(index: Int): Int = entries[index] ?: throw NoSuchElementException()

    public operator fun set(index: Int, value: Int) {
        if (index !in entries.indices) {
            throw ArrayIndexOutOfBoundsException(
                "`index` $index is out of bounds given capacity $capacity."
            )
        }
        entries[index] = value
    }

    public fun toIntArray(): IntArray = entries.compactCopy()

    public fun toShortArray(): ShortArray = toIntArray().map { it.toShort() }.toShortArray()

    public fun toByteArray(): ByteArray = toIntArray().map { it.toByte() }.toByteArray()

    override fun iterator(): Iterator<Int?> = entries.iterator()

    public companion object {
        public const val DEFAULT_CAPACITY: Int = 10

        private fun Array<Int?>.compactCopy(): IntArray {
            val highest = indexOfLast { it != null }
            val copy = IntArray(highest + 1)
            for (i in copy.indices) {
                val value = this[i] ?: error("There must not be a null gap in array: $this")
                copy[i] = value
            }
            return copy
        }
    }
}
