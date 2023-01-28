package org.rsmod.plugins.net.login.upstream

import org.rsmod.plugins.api.prot.builder.login.LoginPacket

sealed class LoginPacketRequest : LoginPacket {

    sealed class AuthType : LoginPacketRequest() {

        object TwoFactorInputTrustDevice : AuthType()
        object TwoFactorInputDoNotTrustDevice : AuthType()
        object TwoFactorCheckDeviceLinkFound : AuthType()
        object TwoFactorCheckDeviceLinkNotFound : AuthType()
        object Skip : AuthType()
    }

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
