package org.rsmod.plugins.net.login.upstream

import org.rsmod.plugins.net.rev.builder.login.LoginPacket

sealed class LoginPacketRequest : LoginPacket {

    data class AuthCode(val code: Int?) : LoginPacketRequest()

    data class CacheChecksum(val crcs: IntArray) : LoginPacketRequest() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CacheChecksum

            return crcs.contentEquals(other.crcs)
        }

        override fun hashCode(): Int {
            return crcs.contentHashCode()
        }
    }
}
