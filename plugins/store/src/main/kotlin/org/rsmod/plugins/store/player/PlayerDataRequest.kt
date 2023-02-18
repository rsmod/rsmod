package org.rsmod.plugins.store.player

public data class PlayerDataRequest(
    val username: String,
    val plaintTextPass: String?,
    val loginXtea: IntArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerDataRequest

        if (username != other.username) return false
        if (plaintTextPass != other.plaintTextPass) return false
        return loginXtea.contentEquals(other.loginXtea)
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + (plaintTextPass?.hashCode() ?: 0)
        result = 31 * result + loginXtea.contentHashCode()
        return result
    }
}
