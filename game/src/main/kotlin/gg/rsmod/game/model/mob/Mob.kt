package gg.rsmod.game.model.mob

import gg.rsmod.game.message.MessageListener
import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.model.client.NpcEntity
import gg.rsmod.game.model.client.PlayerEntity
import gg.rsmod.game.model.map.Coordinates

sealed class Mob

class Player(
    private val messageListeners: List<MessageListener>,
    val loginName: String,
    val entity: PlayerEntity
) : Mob() {

    val username: String
        get() = entity.username

    var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }

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

class Npc(
    val entity: NpcEntity
) : Mob()
