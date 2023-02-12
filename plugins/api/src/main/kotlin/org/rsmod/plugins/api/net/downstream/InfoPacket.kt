package org.rsmod.plugins.api.net.downstream

import org.rsmod.protocol.game.packet.DownstreamPacket

public data class PlayerInfoPacket(val data: ByteArray, val length: Int) : DownstreamPacket {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerInfoPacket

        if (!data.contentEquals(other.data)) return false
        return length == other.length
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + length
        return result
    }
}
