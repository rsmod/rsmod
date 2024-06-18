package org.rsmod.plugins.net.service.upstream

import io.netty.buffer.ByteBuf
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.openrs2.buffer.readString
import org.openrs2.buffer.readVersionedString
import org.openrs2.buffer.use
import org.openrs2.crypto.StreamCipher
import org.openrs2.crypto.SymmetricKey
import org.openrs2.crypto.rsa
import org.openrs2.crypto.xteaDecrypt
import org.rsmod.game.protocol.packet.VariableShortLengthPacketCodec
import org.rsmod.plugins.api.net.builder.login.LoginPacketDecoderMap
import org.rsmod.plugins.api.net.client.ClientType
import org.rsmod.plugins.api.net.client.JavaVendor
import org.rsmod.plugins.api.net.client.OperatingSystem
import org.rsmod.plugins.api.net.client.Platform
import org.rsmod.plugins.api.net.login.LoginPacketRequest
import org.rsmod.plugins.api.net.platform.login.LoginPlatformPacketDecoders
import com.google.inject.Inject
import com.google.inject.Singleton

private const val RANDOM_UUID_BYTE_LENGTH = 24

@Singleton
public class GameLoginCodec @Inject constructor(
    private val key: RSAPrivateCrtKeyParameters,
    private val decoders: LoginPlatformPacketDecoders
) : VariableShortLengthPacketCodec<ServiceRequest.GameLogin>(
    type = ServiceRequest.GameLogin::class.java,
    opcode = 16
) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): ServiceRequest.GameLogin {
        val major = buf.readInt()
        val minor = buf.readInt()
        val clientType = clientTypeForOpcode(buf.readUnsignedByte().toInt())
        val platform = platformForOpcode(buf.readUnsignedByte().toInt())
        buf.skipBytes(Byte.SIZE_BYTES)

        val encryptedLength = buf.readUnsignedShort()
        val encryptedBuf = buf.readSlice(encryptedLength)
        val encrypted = encryptedBuf.rsa(key).use { secure ->
            check(secure.readUnsignedByte().toInt() == 1) { "Invalid RSA header" }
            val xtea = SymmetricKey(secure.readInt(), secure.readInt(), secure.readInt(), secure.readInt())
            val seed = secure.readLong()
            val authDecoder = decoders(platform)[LoginPacketRequest.AuthType::class.java]
                ?: error("AuthType packet decoder must be defined.")
            val authType = authDecoder.decode(secure)
            val authSecret = secure.readAuthSecret(authType)
            secure.skipBytes(Byte.SIZE_BYTES)
            val password = secure.readString()
            return@use ServiceRequest.GameLogin.SecureBlock(xtea, seed, password, authType, authSecret)
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
            val operatingSystem = operatingSystemForOpcode(it.readUnsignedByte().toInt())
            val is64Bit = it.readUnsignedByte().toInt() == 1
            val osVersion = it.readUnsignedShort()
            val javaVendor = javaVendorForOpcode(it.readUnsignedByte().toInt())
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
        val crcDecoder = decoders(platform)[LoginPacketRequest.CacheCrc::class.java]
            ?: error("CacheCrc packet decoder must be defined.")
        val cacheChecksum = crcDecoder.decode(buf)
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
            cacheChecksum = cacheChecksum.crcs
        )
    }

    override fun encode(packet: ServiceRequest.GameLogin, buf: ByteBuf, cipher: StreamCipher) { /* empty */ }

    private fun decoders(platform: Platform): LoginPacketDecoderMap = when (platform) {
        Platform.Desktop -> decoders.desktop
    }

    private fun platformForOpcode(opcode: Int): Platform = when (opcode) {
        0 -> Platform.Desktop
        else -> error("Unhandled platform opcode conversion (opcode=$opcode).")
    }

    private fun javaVendorForOpcode(opcode: Int): JavaVendor = when (opcode) {
        1 -> JavaVendor.Sun
        2 -> JavaVendor.Microsoft
        3 -> JavaVendor.Apple
        5 -> JavaVendor.Oracle
        else -> JavaVendor.Other
    }

    private fun operatingSystemForOpcode(opcode: Int): OperatingSystem = when (opcode) {
        1 -> OperatingSystem.Windows
        2 -> OperatingSystem.Mac
        3 -> OperatingSystem.Linux
        else -> OperatingSystem.Other
    }

    private fun clientTypeForOpcode(opcode: Int): ClientType = when (opcode) {
        1 -> ClientType.RC
        2 -> ClientType.WIP
        3 -> ClientType.BuildLive
        else -> ClientType.Live
    }

    private fun ByteBuf.readAuthSecret(type: LoginPacketRequest.AuthType): Int? {
        val secret: Int?
        when (type) {
            LoginPacketRequest.AuthType.TwoFactorInputTrustDevice,
            LoginPacketRequest.AuthType.TwoFactorInputDoNotTrustDevice -> {
                secret = readUnsignedMedium()
                skipBytes(Byte.SIZE_BYTES)
            }
            LoginPacketRequest.AuthType.TwoFactorCheckDeviceLinkFound -> secret = readInt()
            LoginPacketRequest.AuthType.TwoFactorCheckDeviceLinkNotFound -> {
                secret = null
                skipBytes(Int.SIZE_BYTES)
            }
            LoginPacketRequest.AuthType.Skip -> secret = null
        }
        return secret
    }
}
