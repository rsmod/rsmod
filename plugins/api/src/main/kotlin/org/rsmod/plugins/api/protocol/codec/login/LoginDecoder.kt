package org.rsmod.plugins.api.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.readString0CP1252
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.rsmod.game.config.RsaConfig
import org.rsmod.game.model.client.ClientMachine
import org.rsmod.game.model.client.ClientSettings
import org.rsmod.game.model.client.JavaVendor
import org.rsmod.game.model.client.JavaVersion
import org.rsmod.game.model.client.OperatingSystem
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.codec.ResponseType
import org.rsmod.plugins.api.protocol.packet.login.AuthCode
import org.rsmod.plugins.api.protocol.packet.login.CacheChecksum
import org.rsmod.plugins.api.protocol.packet.login.LoginPacketMap
import org.rsmod.util.security.Xtea
import java.math.BigInteger
import java.util.concurrent.ThreadLocalRandom

private val logger = InlineLogger()

sealed class LoginStage {
    object Handshake : LoginStage()
    object Header : LoginStage()
    object Payload : LoginStage()
    override fun toString(): String = javaClass.simpleName
}

sealed class ConnectionType {
    object Invalid : ConnectionType()
    object Initial : ConnectionType()
    object Login : ConnectionType()
    object Reconnect : ConnectionType()
    override fun toString(): String = javaClass.simpleName
}

class LoginDecoder(
    private val majorRevision: Int,
    private val minorRevision: Int,
    private val rsaConfig: RsaConfig,
    private val cacheCrcs: IntArray,
    private val loginPackets: LoginPacketMap,
    private val serverSeed: Long = ThreadLocalRandom.current().nextLong(),
    private var stage: LoginStage = LoginStage.Handshake,
    private var connectionType: ConnectionType = ConnectionType.Initial,
    private var device: Device = Device.Desktop,
    private var readAttempts: Int = 0,
    private var payloadLength: Int = 0
) : ByteToMessageDecoder() {

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        logger.debug { "Handler added (seed=$serverSeed, channel=${ctx.channel()})" }
        ctx.channel().writeRawResponse(ResponseType.ACCEPTED)
        ctx.channel().writeLoginSeed(serverSeed)
        ctx.channel().flush()
    }

    override fun decode(
        ctx: ChannelHandlerContext,
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        logger.debug { "Decode login request (stage=$stage, channel=${ctx.channel()})" }
        when (stage) {
            LoginStage.Handshake -> ctx.readHandshake(buf, out)
            LoginStage.Header -> ctx.readHeader(buf, out)
            LoginStage.Payload -> ctx.readPayload(buf, out)
        }
    }

    private fun ChannelHandlerContext.readHandshake(buf: ByteBuf, out: MutableList<Any>) {
        val opcode = buf.readUnsignedByte().toInt()
        val connection = handshakeConnectionType(opcode)
        if (!connection.isValid) {
            logger.error { "Invalid connection type (opcode=$opcode, type=$connection, channel=${channel()})" }
            out.add(ResponseType.ERROR_CONNECTING)
            return
        }
        connectionType = connection
        stage = LoginStage.Header
    }

    private fun ChannelHandlerContext.readHeader(buf: ByteBuf, out: MutableList<Any>) {
        if (buf.readableBytes() < Short.SIZE_BYTES) {
            channel().incrementReadAttempts(out)
            return
        }
        payloadLength = buf.readUnsignedShort()
        if (payloadLength == 0) {
            logger.error { "Invalid payload length - must be greater than 0" }
            out.add(ResponseType.JS5_OUT_OF_DATE)
            return
        }
        channel().clearReadAttempts()
        stage = LoginStage.Payload
    }

    private fun ChannelHandlerContext.readPayload(
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        if (buf.readableBytes() < payloadLength) {
            channel().incrementReadAttempts(out)
            return
        }

        val clientMajor = buf.readInt()
        val clientMinor = buf.readInt()
        if (clientMajor != majorRevision || clientMinor != minorRevision) {
            logger.debug {
                val clientRev = "$clientMajor.$clientMinor"
                val serverRev = "$majorRevision.$minorRevision"
                "Client revision mismatch (clientRevision=$clientRev, serverRevision=$serverRev, channel=${channel()})"
            }
            out.add(ResponseType.JS5_OUT_OF_DATE)
            return
        }

        buf.skipBytes(Byte.SIZE_BYTES)

        val deviceOpcode = buf.readUnsignedByte().toInt()
        device = deviceOpcode.device

        val secureBuf = if (rsaConfig.isEnabled) {
            val length = buf.readUnsignedShort()
            val data = ByteArray(length)
            buf.readBytes(data)
            val rsa = BigInteger(data).modPow(rsaConfig.exponent, rsaConfig.modulus)
            Unpooled.wrappedBuffer(rsa.toByteArray())
        } else {
            buf
        }

        val rawPayload = ByteArray(buf.readableBytes())
        buf.readBytes(rawPayload)

        val secureBlock = readSecureBlock(secureBuf)
        if (secureBlock == null) {
            out.add(ResponseType.BAD_SESSION_ID)
            return
        }

        val deciphered = Xtea.decipher(rawPayload, secureBlock.xteas)
        val xteaBuf = Unpooled.wrappedBuffer(deciphered)

        readXteaBlock(secureBlock, xteaBuf, out)
    }

    private fun ChannelHandlerContext.readSecureBlock(buf: ByteBuf): LoginSecureBlock? {
        val handshake = buf.readUnsignedByte().toInt()
        if (handshake != SUCCESSFUL_RSA_HANDSHAKE) {
            logger.error { "Incorrect RSA handshake (opcode=$handshake, channel=${channel()})" }
            return null
        }

        val xteas = IntArray(4) { buf.readInt() }
        val seed = buf.readLong()

        if (seed != serverSeed) {
            logger.debug { "Incorrect handshake seed (clientSeed=$seed, serverSeed=$serverSeed, channel=${channel()})" }
            return null
        }

        val authCode: AuthCode?
        val password: String?
        var reconnectXteas: IntArray? = null

        if (connectionType == ConnectionType.Reconnect) {
            reconnectXteas = IntArray(4) { buf.readInt() }
            authCode = null
            password = null
        } else {
            val handler = loginPackets.getValue<AuthCode>()
            authCode = handler.read(buf)
            buf.skipBytes(Byte.SIZE_BYTES)
            password = buf.readStringCP1252()
        }

        return LoginSecureBlock(
            password = password,
            authCode = authCode?.code,
            xteas = xteas,
            reconnectXteas = reconnectXteas
        )
    }

    private fun ChannelHandlerContext.readXteaBlock(
        secureBlock: LoginSecureBlock,
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        val password = secureBlock.password
        val authCode = secureBlock.authCode
        val xteas = secureBlock.xteas
        val reconnectXteas = secureBlock.reconnectXteas

        val username = buf.readStringCP1252()
        if (username.isBlank()) {
            logger.error { "Invalid blank username input (channel=${channel()})" }
            out.add(ResponseType.TOO_MANY_ATTEMPTS)
            return
        }

        val emailLogin = username.contains("@")
        if (emailLogin && username.length !in VALID_EMAIL_LENGTH) {
            logger.error { "Invalid email (email=$username, channel=${channel()})" }
            out.add(ResponseType.INVALID_CREDENTIALS)
            return
        }

        if (!emailLogin && (username.length !in VALID_USERNAME_LENGTH || username.invalidUsername)) {
            logger.error { "Invalid username (username=$username, channel=${channel()})" }
            out.add(ResponseType.INVALID_CREDENTIALS)
            return
        }

        val settings = buf.readClientSettings()
        val uuid = ByteArray(RANDOM_UUID_BYTE_LENGTH) { buf.readByte() }

        /* `sitesettings` string */
        buf.readStringCP1252()
        buf.skipBytes(Int.SIZE_BYTES)

        val machineInfoHeader = buf.readUnsignedByte().toInt()
        if (machineInfoHeader != MACHINE_INFO_HEADER_VALUE) {
            logger.error { "Invalid machine info header (username=$username, channel=${channel()})" }
            out.add(ResponseType.MACHINE_INFO_HEADER)
            return
        }

        val machine = buf.readClientMachine()

        buf.skipBytes(Byte.SIZE_BYTES)
        buf.skipBytes(Int.SIZE_BYTES)

        val checksumHandler = loginPackets.getValue<CacheChecksum>()
        val checksum = checksumHandler.read(buf)
        val crcs = checksum.crcs

        for (i in crcs.indices) {
            val received = crcs[i]
            val expected = cacheCrcs[i]
            if (received != 0 && received != expected) {
                logger.debug {
                    "Cache crc out-of-date (archive=$i, clientCrc=$received, " +
                        "serverCrc=$expected, username=$username, channel=${channel()})"
                }
                out.add(ResponseType.JS5_OUT_OF_DATE)
                return
            }
        }

        val request = LoginRequest(
            channel = channel(),
            username = username,
            password = password,
            device = device,
            email = emailLogin,
            reconnect = connectionType.isReconnection,
            uuid = uuid,
            authCode = authCode,
            xteas = xteas,
            reconnectXteas = reconnectXteas,
            settings = settings,
            machine = machine
        )
        out.add(request)
    }

    /**
     * Read the values required to create and return [ClientSettings].
     *
     * @receiver the [ByteBuf] that contains the required data, the
     * reading begins at the current [ByteBuf.readerIndex].
     */
    private fun ByteBuf.readClientSettings(): ClientSettings {
        val flags = readByte().toInt()
        val width = readUnsignedShort()
        val height = readUnsignedShort()
        return ClientSettings(
            width = width,
            height = height,
            flags = flags
        )
    }

    /**
     * Read the values required to create and return [ClientMachine].
     *
     * @receiver the [ByteBuf] that contains the required data, the
     * reading begins at the current [ByteBuf.readerIndex].
     */
    private fun ByteBuf.readClientMachine(): ClientMachine {
        val operatingSystem = readUnsignedByte().toInt()
        val is64Bit = readUnsignedByte().toInt() == 1
        val osVersion = readUnsignedShort()
        val javaVendor = readUnsignedByte().toInt()
        val javaVersionMajor = readUnsignedByte().toInt()
        val javaVersionMinor = readUnsignedByte().toInt()
        val javaVersionPatch = readUnsignedByte().toInt()
        readUnsignedByte()
        val maxMemory = readUnsignedShort()
        val cpuCount = readUnsignedByte().toInt()
        readUnsignedMedium()
        readUnsignedShort()
        readString0CP1252()
        readString0CP1252()
        readString0CP1252()
        readString0CP1252()
        readUnsignedByte()
        readUnsignedShort()
        readString0CP1252()
        readString0CP1252()
        readUnsignedByte()
        readUnsignedByte()
        IntArray(3) { readInt() }
        readInt()
        readString0CP1252()

        return ClientMachine(
            operatingSystem = operatingSystem.operatingSystem,
            is64Bit = is64Bit,
            osVersion = osVersion,
            javaVendor = javaVendor.javaVendor,
            javaVersion = JavaVersion(javaVersionMajor, javaVersionMinor, javaVersionPatch),
            maxMemory = maxMemory,
            cpuCount = cpuCount
        )
    }

    private fun Channel.incrementReadAttempts(out: MutableList<Any>) {
        readAttempts++
        if (readAttempts >= MAX_READ_ATTEMPTS) {
            logger.debug {
                "Read attempt limit reached... dropping connection (channel=$this)"
            }
            out.add(ResponseType.COULD_NOT_COMPLETE_LOGIN)
        } else {
            logger.trace { "Increment read attempts (attempts=$readAttempts, channel=$this)" }
        }
    }

    private fun Channel.clearReadAttempts() {
        logger.trace { "Clear read attempts (channel=$this)" }
        readAttempts = 0
    }

    private fun Channel.writeRawResponse(response: ResponseType) {
        write(alloc().buffer(Byte.SIZE_BYTES).writeByte(response.id))
    }

    private fun Channel.writeLoginSeed(seed: Long) {
        write(alloc().buffer(Long.SIZE_BYTES).writeLong(seed))
    }

    companion object {
        /**
         * The login handshake id.
         */
        private const val LOGIN_OPCODE = 16

        /**
         * The reconnection handshake id.
         */
        private const val RECONNECT_OPCODE = 18

        /**
         * The username input length range allowed.
         */
        private val VALID_USERNAME_LENGTH = 1..12

        /**
         * The email input length range allowed.
         */
        private val VALID_EMAIL_LENGTH = 1..32

        /**
         * The maximum amount of times that a step in the login process
         * will retry to read the buffer data if not enough readable
         * bytes are available.
         */
        private const val MAX_READ_ATTEMPTS = 5

        /**
         * The RSA-encrypted data will send this value as a way to make
         * sure the [LoginDecoder] has successfully decrypted it.
         */
        private const val SUCCESSFUL_RSA_HANDSHAKE = 1

        /**
         * The byte length of the random number in the client's cache file
         * `random.dat`.
         */
        private const val RANDOM_UUID_BYTE_LENGTH = 24

        /**
         * The machine info block sends this hardcoded value at the start.
         */
        private const val MACHINE_INFO_HEADER_VALUE = 8

        /**
         * A [Regex] containing the inverse pattern allowed for username logins.
         */
        private val INVALID_USERNAME_REGEX = Regex("[^a-zA-Z\\d ]")

        /**
         * Get the [ConnectionType] based on [opcode].
         */
        fun handshakeConnectionType(opcode: Int): ConnectionType = when (opcode) {
            LOGIN_OPCODE -> ConnectionType.Login
            RECONNECT_OPCODE -> ConnectionType.Reconnect
            else -> ConnectionType.Invalid
        }

        /**
         * Get the [OperatingSystem] type based on the [Int] receiver.
         */
        private val Int.operatingSystem: OperatingSystem
            get() = when (this) {
                1 -> OperatingSystem.Windows
                2 -> OperatingSystem.Mac
                3 -> OperatingSystem.Linux
                else -> OperatingSystem.Other
            }

        /**
         * Get the [JavaVendor] based on the [Int] receiver.
         */
        private val Int.javaVendor: JavaVendor
            get() = when (this) {
                1 -> JavaVendor.Sun
                2 -> JavaVendor.Microsoft
                3 -> JavaVendor.Apple
                5 -> JavaVendor.Oracle
                else -> JavaVendor.Other
            }

        private val Int.device: Device
            get() = when (this) {
                1 -> Device.Ios
                2 -> Device.Android
                else -> Device.Desktop
            }

        private val String.invalidUsername: Boolean
            get() = INVALID_USERNAME_REGEX.containsMatchIn(this)

        private val ConnectionType.isValid: Boolean
            get() = this == ConnectionType.Login || this == ConnectionType.Reconnect

        private val ConnectionType.isReconnection: Boolean
            get() = this == ConnectionType.Reconnect
    }
}
