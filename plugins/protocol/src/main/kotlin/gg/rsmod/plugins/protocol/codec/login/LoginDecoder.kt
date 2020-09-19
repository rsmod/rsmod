package gg.rsmod.plugins.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.cache.util.Xtea
import gg.rsmod.game.config.RsaConfig
import gg.rsmod.game.model.client.ClientMachine
import gg.rsmod.game.model.client.ClientSettings
import gg.rsmod.game.model.client.JavaVendor
import gg.rsmod.game.model.client.JavaVersion
import gg.rsmod.game.model.client.OperatingSystem
import gg.rsmod.plugins.protocol.Device
import gg.rsmod.plugins.protocol.codec.ResponseType
import gg.rsmod.plugins.protocol.codec.writeErrResponse
import io.guthix.buffer.readString0CP1252
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
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
    private val revision: Int,
    private val rsaConfig: RsaConfig,
    private val cacheCrcs: IntArray,
    private val serverSeed: Long = ThreadLocalRandom.current().nextLong(),
    private var stage: LoginStage = LoginStage.Handshake,
    private var connectionType: ConnectionType = ConnectionType.Initial,
    private var readAttempts: Int = 0,
    private var payloadLength: Int = 0
) : ByteToMessageDecoder() {

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        logger.debug { "Handler added (seed=$serverSeed, channel=${ctx.channel()})" }
        ctx.channel().writeLoginSeed(serverSeed)
    }

    override fun decode(
        ctx: ChannelHandlerContext,
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        logger.debug { "Decode login request (stage=$stage, channel=${ctx.channel()})" }
        when (stage) {
            LoginStage.Handshake -> ctx.readHandshake(buf)
            LoginStage.Header -> ctx.readHeader(buf)
            LoginStage.Payload -> ctx.readPayload(buf, out)
        }
    }

    private fun ChannelHandlerContext.readHandshake(buf: ByteBuf) {
        val opcode = buf.readUnsignedByte().toInt()
        val connection = handshakeConnectionType(opcode)
        if (!connection.isValid) {
            logger.error { "Invalid connection type (opcode=$opcode, type=$connection, channel=${channel()})" }
            channel().writeErrResponse(ResponseType.ERROR_CONNECTING)
            return
        }
        stage = LoginStage.Header
    }

    private fun ChannelHandlerContext.readHeader(buf: ByteBuf) {
        if (buf.readableBytes() < Short.SIZE_BYTES) {
            channel().incrementReadAttempts()
            return
        }
        payloadLength = buf.readUnsignedShort()
        if (payloadLength == 0) {
            logger.error { "Invalid payload length - must be greater than 0" }
            channel().writeErrResponse(ResponseType.JS5_OUT_OF_DATE)
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
            channel().incrementReadAttempts()
            return
        }

        val clientRevision = buf.readInt()
        if (clientRevision != revision) {
            logger.debug {
                "Client revision mismatch " +
                    "(clientRevision=$clientRevision, serverRevision=$revision, channel=${channel()})"
            }
            channel().writeErrResponse(ResponseType.JS5_OUT_OF_DATE)
            return
        }

        val constant = buf.readInt()
        if (constant != 1) {
            logger.debug { "Invalid constant value (constant=$constant, channel=${channel()})" }
            channel().writeErrResponse(ResponseType.JS5_OUT_OF_DATE)
            return
        }

        buf.skipBytes(Byte.SIZE_BYTES)

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
            channel().writeErrResponse(ResponseType.BAD_SESSION_ID)
            return
        }

        val deciphered = Xtea.decipher(rawPayload, secureBlock.xtea)
        val xteaBuf = Unpooled.wrappedBuffer(deciphered)

        readXteaBlock(secureBlock, xteaBuf, out)
    }

    private fun ChannelHandlerContext.readSecureBlock(buf: ByteBuf): LoginSecureBlock? {
        val handshake = buf.readUnsignedByte().toInt()
        if (handshake != SUCCESSFUL_RSA_HANDSHAKE) {
            logger.error { "Incorrect RSA handshake (opcode=$handshake, channel=${channel()})" }
            return null
        }

        val xtea = IntArray(4) { buf.readInt() }
        val seed = buf.readLong()

        if (seed != serverSeed) {
            logger.debug { "Incorrect handshake seed (clientSeed=$seed, serverSeed=$serverSeed, channel=${channel()})" }
            return null
        }

        val authCode: Int?
        val password: String?

        if (connectionType == ConnectionType.Reconnect) {
            authCode = -1
            password = null
        } else {
            val authType = buf.readByte().toInt()
            authCode = when (authType) {
                0, 2 -> {
                    val auth = buf.readUnsignedMedium()
                    buf.skipBytes(Byte.SIZE_BYTES)
                    auth
                }
                else -> buf.readInt()
            }
            buf.skipBytes(Byte.SIZE_BYTES)
            password = buf.readStringCP1252()
        }

        return LoginSecureBlock(
            password = password,
            authCode = authCode,
            xtea = xtea
        )
    }

    private fun ChannelHandlerContext.readXteaBlock(
        secureBlock: LoginSecureBlock,
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        val password = secureBlock.password
        val authCode = secureBlock.authCode
        val xteas = secureBlock.xtea

        val username = buf.readStringCP1252()
        if (username.isBlank()) {
            logger.error { "Invalid blank username input (channel=${channel()})" }
            channel().writeErrResponse(ResponseType.TOO_MANY_ATTEMPTS)
            return
        }

        val emailLogin = username.contains("@")
        if (emailLogin && username.length !in VALID_EMAIL_LENGTH) {
            logger.error { "Invalid email (email=$username, channel=${channel()})" }
            channel().writeErrResponse(ResponseType.INVALID_CREDENTIALS)
            return
        }

        if (!emailLogin && (username.length !in VALID_USERNAME_LENGTH || username.invalidUsername)) {
            logger.error { "Invalid username (username=$username, channel=${channel()})" }
            channel().writeErrResponse(ResponseType.INVALID_CREDENTIALS)
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
            channel().writeErrResponse(ResponseType.MACHINE_INFO_HEADER)
            return
        }

        val machine = buf.readClientMachine()

        buf.skipBytes(Byte.SIZE_BYTES)
        buf.skipBytes(Int.SIZE_BYTES)

        // TODO: verify integrity, values are now scrambled
        val crcs = IntArray(cacheCrcs.size) { buf.readInt() }

        // TODO: get device based on param in login process
        val device = Device.Desktop

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

    private fun Channel.incrementReadAttempts() {
        readAttempts++
        if (readAttempts >= MAX_READ_ATTEMPTS) {
            logger.debug {
                "Read attempt limit reached... dropping connection (channel=$this)"
            }
            writeErrResponse(ResponseType.COULD_NOT_COMPLETE_LOGIN)
        } else {
            logger.trace { "Increment read attempts (attempts=$readAttempts, channel=$this)" }
        }
    }

    private fun Channel.clearReadAttempts() {
        logger.trace { "Clear read attempts (channel=$this)" }
        readAttempts = 0
    }

    private fun Channel.writeLoginSeed(seed: Long) {
        writeAndFlush(alloc().buffer(Byte.SIZE_BYTES).writeByte(0))
        writeAndFlush(alloc().buffer(Long.SIZE_BYTES).writeLong(seed))
    }

    private val String.invalidUsername: Boolean
        get() = INVALID_USERNAME_REGEX.containsMatchIn(this)

    private val ConnectionType.isValid: Boolean
        get() = this == ConnectionType.Login || this == ConnectionType.Reconnect

    private val ConnectionType.isReconnection: Boolean
        get() = this == ConnectionType.Reconnect

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
    }
}
