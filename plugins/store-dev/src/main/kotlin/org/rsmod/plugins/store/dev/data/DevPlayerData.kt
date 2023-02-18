package org.rsmod.plugins.store.dev.data

import org.rsmod.plugins.store.player.PlayerCodecData

public data class DevPlayerData(
    public val username: String,
    public val displayName: String,
    public val coords: IntArray
) : PlayerCodecData {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DevPlayerData

        if (username != other.username) return false
        if (displayName != other.displayName) return false
        return coords.contentEquals(other.coords)
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + coords.contentHashCode()
        return result
    }
}
