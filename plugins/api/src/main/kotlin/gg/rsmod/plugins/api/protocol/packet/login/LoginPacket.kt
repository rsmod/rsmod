package gg.rsmod.plugins.api.protocol.packet.login

sealed class LoginPacket

data class AuthCode(val code: Int) : LoginPacket()

data class CacheChecksum(val crcs: IntArray) : LoginPacket() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CacheChecksum

        if (!crcs.contentEquals(other.crcs)) return false

        return true
    }

    override fun hashCode(): Int = crcs.contentHashCode()
}
