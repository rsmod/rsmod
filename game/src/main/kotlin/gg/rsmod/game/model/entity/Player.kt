package gg.rsmod.game.model.entity

import gg.rsmod.game.message.MessageListener
import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.model.map.Coordinates

class Player(
    private val loginName: String,
    private var displayName: String = loginName,
    private val messageListeners: List<MessageListener>,
    var coords: Coordinates = Coordinates.ZERO
) {

    val username: String
        get() = displayName

    fun write(packet: ServerPacket) {
        messageListeners.forEach { listener ->
            listener.write(packet)
        }
    }

    fun flush() {
        messageListeners.forEach { listener ->
            listener.flush()
        }
    }
}
