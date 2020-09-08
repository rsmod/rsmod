package gg.rsmod.plugins.protocol.codec.login

import gg.rsmod.game.model.ClientMachine
import gg.rsmod.game.model.ClientSettings
import io.netty.channel.Channel

/**
 * Responsible for holding sensitive data during the log in process.
 *
 * @param password the password used to attempt log in. Can be null if
 * the request is a reconnection request.
 *
 * @param authCode the auth code used to attempt log in.
 *
 * @param xtea the XTEA key received from the client, if the request is
 * a reconnection, the key sent will be the one from the last successful
 * log in.
 */
data class LoginSecureBlock(
    val password: String?,
    val authCode: Int?,
    val xtea: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginSecureBlock

        if (password != other.password) return false
        if (authCode != other.authCode) return false
        if (!xtea.contentEquals(other.xtea)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = password?.hashCode() ?: 0
        result = 31 * result + (authCode ?: 0)
        result = 31 * result + xtea.contentHashCode()
        return result
    }
}

data class LoginRequest(
    val channel: Channel,
    val username: String,
    val password: String?,
    val reconnecting: Boolean,
    val uuid: ByteArray,
    val authCode: Int?,
    val xtea: IntArray,
    val settings: ClientSettings,
    val machine: ClientMachine
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginRequest

        if (channel != other.channel) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (reconnecting != other.reconnecting) return false
        if (!uuid.contentEquals(other.uuid)) return false
        if (authCode != other.authCode) return false
        if (!xtea.contentEquals(other.xtea)) return false
        if (settings != other.settings) return false
        if (machine != other.machine) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channel.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + reconnecting.hashCode()
        result = 31 * result + uuid.contentHashCode()
        result = 31 * result + (authCode ?: 0)
        result = 31 * result + xtea.contentHashCode()
        result = 31 * result + settings.hashCode()
        result = 31 * result + machine.hashCode()
        return result
    }
}

/**
 * Represents a server-response to a successful log in request.
 *
 * @param playerIndex the player index for the channel attempting
 * to log in. This value should start from 1 and not 0.
 *
 * @param privilege the privilege level of the player associated
 * with the channel.
 */
data class LoginResponse(
    val privilege: Int,
    val playerIndex: Int
)
