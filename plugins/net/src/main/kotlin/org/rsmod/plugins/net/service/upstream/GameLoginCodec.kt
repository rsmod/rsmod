package org.rsmod.plugins.net.service.upstream

import io.netty.buffer.ByteBuf
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.openrs2.buffer.readIntAlt3
import org.openrs2.buffer.readIntAlt3Reverse
import org.openrs2.buffer.readString
import org.openrs2.buffer.readVersionedString
import org.openrs2.buffer.use
import org.openrs2.crypto.StreamCipher
import org.openrs2.crypto.XteaKey
import org.openrs2.crypto.rsa
import org.openrs2.crypto.xteaDecrypt
import org.rsmod.protocol.packet.VariableShortLengthPacketCodec
import javax.inject.Inject
import javax.inject.Singleton

private const val CACHE_ARCHIVE_COUNT = 21
private const val RANDOM_UUID_BYTE_LENGTH = 24

@Singleton
class GameLoginCodec @Inject constructor(
    private val key: RSAPrivateCrtKeyParameters
) : VariableShortLengthPacketCodec<ServiceRequest.GameLogin>(
    type = ServiceRequest.GameLogin::class.java,
    opcode = 16
) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): ServiceRequest.GameLogin {
        val major = buf.readInt()
        val minor = buf.readInt()
        val clientType = buf.readUnsignedByte().toInt()
        val platform = buf.readUnsignedByte().toInt()
        buf.skipBytes(Byte.SIZE_BYTES)

        val encryptedLength = buf.readUnsignedShort()
        val encryptedBuf = buf.readSlice(encryptedLength)
        val encrypted = encryptedBuf.rsa(key).use { secure ->
            check(secure.readUnsignedByte().toInt() == 1) { "Invalid RSA header" }
            val xtea = XteaKey(secure.readInt(), secure.readInt(), secure.readInt(), secure.readInt())
            val seed = secure.readLong()
            val authCode: Int?
            when (secure.readUnsignedByte().toInt()) {
                1, 3 -> {
                    authCode = secure.readUnsignedMedium()
                    secure.skipBytes(Byte.SIZE_BYTES)
                }
                0 -> {
                    // TODO: remember device
                    authCode = secure.readUnsignedMedium()
                    secure.skipBytes(Byte.SIZE_BYTES)
                }
                2 -> {
                    authCode = null
                    secure.skipBytes(Int.SIZE_BYTES)
                }
                else -> authCode = null
            }
            secure.skipBytes(Byte.SIZE_BYTES)
            val password = secure.readString()
            return@use ServiceRequest.GameLogin.SecureBlock(xtea, seed, password, authCode)
        }

        buf.xteaDecrypt(buf.readerIndex(), buf.readableBytes(), encrypted.xtea)

        val username = buf.readString()
        val clientInfo = buf.let {
            val resizable = it.readUnsignedByte().toInt() shr 1 == 1
            val width = it.readUnsignedShort()
            val height = it.readUnsignedShort()
            ServiceRequest.GameLogin.ClientInfo(resizable, width, height)
        }
        val randomDat = ByteArray(RANDOM_UUID_BYTE_LENGTH) { buf.readByte() }
        val siteSettings = buf.readString()
        buf.skipBytes(Int.SIZE_BYTES)

        val machineInfo = buf.let {
            val version = it.readUnsignedByte().toInt()
            val operatingSystem = it.readUnsignedByte().toInt()
            val is64Bit = it.readUnsignedByte().toInt() == 1
            val osVersion = it.readUnsignedShort()
            val javaVendor = it.readUnsignedByte().toInt()
            val javaVersionMajor = it.readUnsignedByte().toInt()
            val javaVersionMinor = it.readUnsignedByte().toInt()
            val javaVersionPatch = it.readUnsignedByte().toInt()
            it.readUnsignedByte()
            val maxMemory = it.readUnsignedShort()
            val cpuCount = it.readUnsignedByte().toInt()
            it.readUnsignedMedium()
            it.readUnsignedShort()
            it.readVersionedString()
            it.readVersionedString()
            it.readVersionedString()
            it.readVersionedString()
            it.readUnsignedByte()
            it.readUnsignedShort()
            it.readVersionedString()
            it.readVersionedString()
            it.readUnsignedByte()
            it.readUnsignedByte()
            repeat(3) { _ -> it.readInt() }
            it.readInt()
            it.readVersionedString()
            it.readVersionedString()
            ServiceRequest.GameLogin.MachineInfo(
                version = version,
                operatingSystem = operatingSystem,
                is64Bit = is64Bit,
                osVersion = osVersion,
                javaVendor = javaVendor,
                javaVersionMajor = javaVersionMajor,
                javaVersionMinor = javaVersionMinor,
                javaVersionPatch = javaVersionPatch,
                maxMemory = maxMemory,
                cpuCount = cpuCount
            )
        }
        buf.skipBytes(Byte.SIZE_BYTES) /* `clientType` - written twice for some reason */
        buf.skipBytes(Int.SIZE_BYTES)
        val cacheCrc = IntArray(CACHE_ARCHIVE_COUNT).apply {
            this[13] = buf.readIntAlt3()
            this[2] = buf.readInt()
            this[19] = buf.readIntAlt3Reverse()
            this[8] = buf.readInt()
            this[5] = buf.readIntAlt3()
            buf.skipBytes(Int.SIZE_BYTES)
            this[1] = buf.readIntLE()
            this[15] = buf.readIntAlt3Reverse()
            this[10] = buf.readInt()
            this[0] = buf.readIntAlt3()
            this[18] = buf.readIntLE()
            this[6] = buf.readIntAlt3()
            this[3] = buf.readIntAlt3Reverse()
            this[11] = buf.readIntLE()
            this[7] = buf.readIntAlt3Reverse()
            this[9] = buf.readInt()
            this[14] = buf.readIntLE()
            this[17] = buf.readIntLE()
            this[20] = buf.readIntAlt3Reverse()
            this[4] = buf.readInt()
            this[12] = buf.readIntLE()
        }
        return ServiceRequest.GameLogin(
            buildMajor = major,
            buildMinor = minor,
            clientType = clientType,
            platform = platform,
            encrypted = encrypted,
            username = username,
            clientInfo = clientInfo,
            siteSettings = siteSettings,
            randomDat = randomDat,
            machineInfo = machineInfo,
            cacheCrc = cacheCrc
        )
    }

    override fun encode(packet: ServiceRequest.GameLogin, buf: ByteBuf, cipher: StreamCipher) {
        TODO("Implement for testing purposes")
    }
}
