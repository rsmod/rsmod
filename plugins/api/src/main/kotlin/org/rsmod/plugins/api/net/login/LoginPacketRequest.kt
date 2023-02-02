package org.rsmod.plugins.api.net.login

import org.rsmod.plugins.api.net.builder.login.LoginPacket

public sealed class LoginPacketRequest : LoginPacket {

    public sealed class AuthType : LoginPacketRequest() {

        public object TwoFactorInputTrustDevice : AuthType()
        public object TwoFactorInputDoNotTrustDevice : AuthType()
        public object TwoFactorCheckDeviceLinkFound : AuthType()
        public object TwoFactorCheckDeviceLinkNotFound : AuthType()
        public object Skip : AuthType()
    }

    public data class CacheChecksum(val crcs: IntArray) : LoginPacketRequest() {

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
