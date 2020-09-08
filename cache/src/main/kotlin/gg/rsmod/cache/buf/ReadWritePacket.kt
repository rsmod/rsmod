package gg.rsmod.cache.buf

/**
 * A read-write packet is a buffer containing an array of bytes and the same
 * pointer to the backing array.
 */
internal class ReadWritePacket(buffer: ByteArray) {

    /**
     * The [WriteOnlyPacket] used to write to this packet.
     */
    private val writer = WriteOnlyPacket(buffer)

    /**
     * The [ReadOnlyPacket] used to read from this packet.
     */
    private val reader = ReadOnlyPacket(buffer)

    /**
     * Create a [ReadWritePacket] with a backing-array the size of [capacity].
     */
    constructor(capacity: Int) : this(ByteArray(capacity))

    /**
     * Alias for this packet's writer [WriteOnlyPacket.data].
     */
    val data: ByteArray
        get() = writer.data

    /**
     * Reset the position for both the reader and writer packet.
     */
    fun reset(): ReadWritePacket {
        writer.reset()
        reader.reset()
        return this
    }

    /**
     * Alias for this packet's writer [WriteOnlyPacket.position]
     * setter.
     */
    fun setWriterPosition(position: Int) {
        writer.position = position
    }

    /**
     * Alias for this packet's reader [ReadOnlyPacket.position]
     * setter.
     */
    fun setReaderPosition(position: Int) {
        reader.position = position
    }

    /**
     * @see [ReadOnlyPacket.g1s]
     */
    val g1s: Int
        get() = reader.g1s

    /**
     * @see [ReadOnlyPacket.g1s_altA]
     */
    val g1s_altA: Int
        get() = reader.g1s_altA

    /**
     * @see [ReadOnlyPacket.g1s_altC]
     */
    val g1s_altC: Int
        get() = reader.g1s_altC

    /**
     * @see [ReadOnlyPacket.g1s_altS]
     */
    val g1s_altS: Int
        get() = reader.g1s_altS

    /**
     * @see [ReadOnlyPacket.g1]
     */
    val g1: Int
        get() = reader.g1

    /**
     * @see [ReadOnlyPacket.g1_altA]
     */
    val g1_altA: Int
        get() = reader.g1_altA

    /**
     * @see [ReadOnlyPacket.g1_altC]
     */
    val g1_altC: Int
        get() = reader.g1_altC

    /**
     * @see [ReadOnlyPacket.g1_altS]
     */
    val g1_altS: Int
        get() = reader.g1_altS

    /**
     * @see [ReadOnlyPacket.g2s]
     */
    val g2s: Int
        get() = reader.g2s

    /**
     * @see [ReadOnlyPacket.g2sLE]
     */
    val g2sLE: Int
        get() = reader.g2sLE

    /**
     * @see [ReadOnlyPacket.g2]
     */
    val g2: Int
        get() = reader.g2

    /**
     * @see [ReadOnlyPacket.g2_altA]
     */
    val g2_altA: Int
        get() = reader.g2_altA

    /**
     * @see [ReadOnlyPacket.g2LE]
     */
    val g2LE: Int
        get() = reader.g2LE

    /**
     * @see [ReadOnlyPacket.g2LE_altA]
     */
    val g2LE_altA: Int
        get() = reader.g2LE_altA

    /**
     * @see [ReadOnlyPacket.g3s]
     */
    val g3s: Int
        get() = reader.g3s

    /**
     * @see [ReadOnlyPacket.g3]
     */
    val g3: Int
        get() = reader.g3

    /**
     * @see [ReadOnlyPacket.g4]
     */
    val g4: Int
        get() = reader.g4

    /**
     * @see [ReadOnlyPacket.g4_alt1]
     */
    val g4_alt1: Int
        get() = reader.g4_alt1

    /**
     * @see [ReadOnlyPacket.g4_alt2]
     */
    val g4_alt2: Int
        get() = reader.g4_alt2

    /**
     * @see [ReadOnlyPacket.g4_alt3]
     */
    val g4_alt3: Int
        get() = reader.g4_alt3

    /**
     * @see [ReadOnlyPacket.gsmart1or2]
     */
    val gsmart1or2: Int
        get() = reader.gsmart1or2

    /**
     * @see [ReadOnlyPacket.gsmart2or4]
     */
    val gsmart2or4: Int
        get() = reader.gsmart2or4

    /**
     * @see [ReadOnlyPacket.g8]
     */
    val g8: Long
        get() = reader.g8

    /**
     * @see [ReadOnlyPacket.gfloat]
     */
    val gfloat: Float
        get() = reader.gfloat

    /**
     * @see [ReadOnlyPacket.gjstr]
     */
    val gjstr: String
        get() = reader.gjstr

    /**
     * @see [ReadOnlyPacket.gdata]
     */
    fun gdata(dst: ByteArray, position: Int, length: Int) = reader.gdata(dst, position, length)

    /**
     * @see [ReadOnlyPacket.gdata]
     */
    fun gdata(dst: ByteArray) = reader.gdata(dst)

    /**
     * @see [WriteOnlyPacket.p1]
     */
    fun p1(value: Int) = writer.p1(value)

    /**
     * @see [WriteOnlyPacket.p1_altA]
     */
    fun p1_altA(value: Int) = writer.p1_altA(value)

    /**
     * @see [WriteOnlyPacket.p1_altC]
     */
    fun p1_altC(value: Int) = writer.p1_altC(value)

    /**
     * @see [WriteOnlyPacket.p1_altS]
     */
    fun p1_altS(value: Int) = writer.p1_altS(value)

    /**
     * @see [WriteOnlyPacket.p2]
     */
    fun p2(value: Int) = writer.p2(value)

    /**
     * @see [WriteOnlyPacket.p2_altA]
     */
    fun p2_altA(value: Int) = writer.p2_altA(value)

    /**
     * @see [WriteOnlyPacket.p2LE]
     */
    fun p2LE(value: Int) = writer.p2LE(value)

    /**
     * @see [WriteOnlyPacket.p2LE_altA]
     */
    fun p2LE_altA(value: Int) = writer.p2LE_altA(value)

    /**
     * @see [WriteOnlyPacket.p3]
     */
    fun p3(value: Int) = writer.p3(value)

    /**
     * @see [WriteOnlyPacket.p4]
     */
    fun p4(value: Int) = writer.p4(value)

    /**
     * @see [WriteOnlyPacket.p4_alt1]
     */
    fun p4_alt1(value: Int) = writer.p4_alt1(value)

    /**
     * @see [WriteOnlyPacket.p4_alt2]
     */
    fun p4_alt2(value: Int) = writer.p4_alt2(value)

    /**
     * @see [WriteOnlyPacket.p4_alt3]
     */
    fun p4_alt3(value: Int) = writer.p4_alt3(value)

    /**
     * @see [WriteOnlyPacket.p5]
     */
    fun p5(value: Long) = writer.p5(value)

    /**
     * @see [WriteOnlyPacket.p8]
     */
    fun p8(value: Long) = writer.p8(value)

    /**
     * @see [WriteOnlyPacket.pjstr]
     */
    fun pjstr(value: String) = writer.pjstr(value)

    /**
     * @see [WriteOnlyPacket.psmart1or2]
     */
    fun psmart1or2(value: Int) = writer.psmart1or2(value)

    /**
     * @see [WriteOnlyPacket.psmart2or4]
     */
    fun psmart2or4(value: Int) = writer.psmart2or4(value)

    /**
     * @see [WriteOnlyPacket.gdata]
     */
    fun pdata(src: ByteArray, srcOffset: Int, length: Int) = writer.pdata(src, srcOffset, length)

    /**
     * @see [WriteOnlyPacket.gdata]
     */
    fun pdata(src: ByteArray) = writer.pdata(src)

    /**
     * @see [WriteOnlyPacket.pdataLE_altA]
     */
    fun pdataLE_altA(dst: ByteArray, position: Int, length: Int) = reader.pdataLE_altA(dst, position, length)

    /**
     * @see [WriteOnlyPacket.pbits]
     */
    fun pbits(value: Int, numBits: Int) = writer.pbits(value, numBits)

    /**
     * @see [WriteOnlyPacket.setBitMode]
     */
    fun setBitMode(bitMode: Boolean) = writer.setBitMode(bitMode)

    companion object {

        /**
         * Create a [ReadWritePacket] with [data] as its data for both
         * the backing [WriteOnlyPacket] and [ReadOnlyPacket].
         */
        fun from(data: ByteArray): ReadWritePacket = ReadWritePacket(data)
    }
}
