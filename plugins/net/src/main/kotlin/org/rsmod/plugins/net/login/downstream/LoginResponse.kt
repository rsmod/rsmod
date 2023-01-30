package org.rsmod.plugins.net.login.downstream

import org.openrs2.crypto.StreamCipher
import org.rsmod.protocol.game.packet.Packet

sealed class LoginResponse : Packet {

    /**
     * @param deviceLinkIdentifier value stored in client's preference file along with bit-packed
     * username as key.
     *
     * @param playerModLevel value used by client to enable certain features.
     * Value 0: player - Value 1: p-mod - Value 2: j-mod.
     *
     * @param cipher the [StreamCipher] used to encode or decode [deviceLinkIdentifier].
     */
    data class ConnectOk(
        val deviceLinkIdentifier: Int?,
        val playerModLevel: Int,
        val playerMod: Boolean,
        val playerIndex: Int,
        val playerMember: Boolean,
        val accountHash: Long,
        val cipher: StreamCipher
    ) : LoginResponse()

    object ClientOutOfDate : LoginResponse()
    object BadSessionId : LoginResponse()
    object ClientProtocolOutOfDate : LoginResponse()
}
