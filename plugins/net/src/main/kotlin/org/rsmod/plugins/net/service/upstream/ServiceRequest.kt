package org.rsmod.plugins.net.service.upstream

import org.openrs2.crypto.XteaKey
import org.rsmod.plugins.net.game.client.ClientType
import org.rsmod.plugins.net.game.client.JavaVendor
import org.rsmod.plugins.net.game.client.OperatingSystem
import org.rsmod.plugins.net.game.client.Platform
import org.rsmod.plugins.net.login.upstream.LoginPacketRequest
import org.rsmod.protocol.packet.Packet

sealed class ServiceRequest : Packet {

    data class InitJs5RemoteConnection(val build: Int) : ServiceRequest()
    object InitGameConnection : ServiceRequest()

    data class GameLogin(
        val buildMajor: Int,
        val buildMinor: Int,
        val clientType: ClientType,
        val platform: Platform,
        val encrypted: SecureBlock,
        val username: String,
        val clientInfo: ClientInfo,
        val randomDat: ByteArray,
        val siteSettings: String,
        val machineInfo: MachineInfo,
        val cacheCrc: IntArray
    ) : ServiceRequest() {

        data class SecureBlock(
            val xtea: XteaKey,
            val seed: Long,
            val password: String,
            val authType: LoginPacketRequest.AuthType,
            val authCode: Int?
        )

        data class ClientInfo(
            val resizable: Boolean,
            val width: Int,
            val height: Int
        )

        data class MachineInfo(
            val version: Int,
            val operatingSystem: OperatingSystem,
            val is64Bit: Boolean,
            val osVersion: Int,
            val javaVendor: JavaVendor,
            val javaVersionMajor: Int,
            val javaVersionMinor: Int,
            val javaVersionPatch: Int,
            val maxMemory: Int,
            val cpuCount: Int
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GameLogin

            if (buildMajor != other.buildMajor) return false
            if (buildMinor != other.buildMinor) return false
            if (clientType != other.clientType) return false
            if (platform != other.platform) return false
            if (encrypted != other.encrypted) return false
            if (username != other.username) return false
            if (clientInfo != other.clientInfo) return false
            if (!randomDat.contentEquals(other.randomDat)) return false
            if (siteSettings != other.siteSettings) return false
            if (machineInfo != other.machineInfo) return false
            return cacheCrc.contentEquals(other.cacheCrc)
        }

        override fun hashCode(): Int {
            var result = buildMajor
            result = 31 * result + buildMinor
            result = 31 * result + clientType.hashCode()
            result = 31 * result + platform.hashCode()
            result = 31 * result + encrypted.hashCode()
            result = 31 * result + username.hashCode()
            result = 31 * result + clientInfo.hashCode()
            result = 31 * result + randomDat.contentHashCode()
            result = 31 * result + siteSettings.hashCode()
            result = 31 * result + machineInfo.hashCode()
            result = 31 * result + cacheCrc.contentHashCode()
            return result
        }
    }
}
