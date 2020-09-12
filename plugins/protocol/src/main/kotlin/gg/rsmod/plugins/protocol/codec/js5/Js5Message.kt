package gg.rsmod.plugins.protocol.codec.js5

import com.google.common.base.MoreObjects

/**
 * Represents a request from a JS5 connection.
 *
 * @param packed a bit-packed value of the request data.
 */
inline class Js5Request(private val packed: Int) {

    /**
     * Extract the archive id from the [packed] value.
     */
    val archive: Int
        get() = packed and 0x7FFF

    /**
     * Extract the group id from the [packed] value.
     */
    val group: Int
        get() = (packed shr 15) and 0xFFFF

    /**
     * Extract the urgent flag from the [packed] value.
     */
    val urgent: Boolean
        get() = ((packed shr 31) and 0x1) != 0

    /**
     * Construct a request and pack the values into a single int.
     *
     * @param archive the cache archive id. Takes up the bits ranging
     * from 0-14 in the [packed] int.
     *
     * @param group the archive's group id. Takes up the bits ranging
     * from 15-30 in the [packed] int.
     *
     * @param urgent the flag that notifies our dispatcher that this
     * request is urgent and should be added to the front of the
     * queue. Takes up the last bit in the [packed] int.
     */
    constructor(archive: Int, group: Int, urgent: Boolean) :
        this((archive and 0x7FFF) or ((group and 0xFFFF) shl 15) or ((urgent.toInt and 0x1) shl 31))

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("archive", archive)
        .add("group", group)
        .add("urgent", urgent)
        .toString()

    companion object {
        private val Boolean.toInt
            get() = if (this) 1 else 0
    }
}

data class Js5Response(
    val archive: Int,
    val group: Int,
    val compressionType: Int,
    val compressedLength: Int,
    val data: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Js5Response

        if (compressionType != other.compressionType) return false
        if (compressedLength != other.compressedLength) return false
        if (archive != other.archive) return false
        if (group != other.group) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = compressionType
        result = 31 * result + compressedLength
        result = 31 * result + archive
        result = 31 * result + group
        result = 31 * result + data.contentHashCode()
        return result
    }

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("archive", archive)
        .add("group", group)
        .add("compression", compressionType)
        .add("compressedLength", compressedLength)
        .add("rawLength", data.size)
        .toString()
}
