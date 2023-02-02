package org.rsmod.plugins.api.cache.map.xtea

import com.fasterxml.jackson.annotation.JsonProperty

internal data class XteaFile(
    @JsonProperty("mapsquare") val mapSquare: Int,
    val key: IntArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as XteaFile

        if (mapSquare != other.mapSquare) return false
        return key.contentEquals(other.key)
    }

    override fun hashCode(): Int {
        var result = mapSquare
        result = 31 * result + key.contentHashCode()
        return result
    }
}
