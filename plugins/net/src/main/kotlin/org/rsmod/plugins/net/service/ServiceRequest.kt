package org.rsmod.plugins.net.service

import org.openrs2.crypto.XteaKey
import org.rsmod.protocol.packet.Packet

sealed class ServiceRequest : Packet {

    data class InitJs5RemoteConnection(val build: Int) : ServiceRequest()
    object InitGameConnection : ServiceRequest()

    data class LogIn(
        val buildMajor: Int,
        val buildMinor: Int,
        val device: Int,
        val encrypted: SecureBlock,
        val username: String,
        val clientInfo: ClientInfo,
        val randomDat: ByteArray,
        val siteSettings: String,
        val machineInfo: MachineInfo,
        val confClientType: Int,
        val cacheCrc: IntArray
    ) : ServiceRequest() {

        data class SecureBlock(
            val xtea: XteaKey,
            val seed: Long,
            val password: String,
            val authCode: Int?,
        )

        data class ClientInfo(
            val resizable: Boolean,
            val width: Int,
            val height: Int
        )

        data class MachineInfo(
            val version: Int,
            val operatingSystem: Int,
            val is64Bit: Boolean,
            val osVersion: Int,
            val javaVendor: Int,
            val javaVersionMajor: Int,
            val javaVersionMinor: Int,
            val javaVersionPatch: Int,
            val maxMemory: Int,
            val cpuCount: Int
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LogIn

            if (buildMajor != other.buildMajor) return false
            if (buildMinor != other.buildMinor) return false
            if (device != other.device) return false
            if (encrypted != other.encrypted) return false
            if (username != other.username) return false
            if (clientInfo != other.clientInfo) return false
            if (!randomDat.contentEquals(other.randomDat)) return false
            if (siteSettings != other.siteSettings) return false
            if (machineInfo != other.machineInfo) return false
            if (confClientType != other.confClientType) return false
            if (!cacheCrc.contentEquals(other.cacheCrc)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = buildMajor
            result = 31 * result + buildMinor
            result = 31 * result + device
            result = 31 * result + encrypted.hashCode()
            result = 31 * result + username.hashCode()
            result = 31 * result + clientInfo.hashCode()
            result = 31 * result + randomDat.contentHashCode()
            result = 31 * result + siteSettings.hashCode()
            result = 31 * result + machineInfo.hashCode()
            result = 31 * result + confClientType
            result = 31 * result + cacheCrc.contentHashCode()
            return result
        }
    }
}
