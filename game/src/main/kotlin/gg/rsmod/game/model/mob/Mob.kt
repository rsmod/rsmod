package gg.rsmod.game.model.mob

import gg.rsmod.game.event.EventBus
import gg.rsmod.game.event.impl.LoginEvent
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

    var index: Int
        get() = entity.index
        set(value) { entity.index = value }

    var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }

    fun login(eventBus: EventBus) {
        eventBus.publish(LoginEvent(this, LoginEvent.Stage.Priority))
        eventBus.publish(LoginEvent(this, LoginEvent.Stage.Normal))
        eventBus.publish(LoginEvent(this, LoginEvent.Stage.Delayed))
    }

    fun write(packet: ServerPacket) {
        messageListeners.forEach { it.write(packet) }
    }

    fun flush() {
        messageListeners.forEach { it.flush() }
    }
}

class Npc(
    val entity: NpcEntity
) : Mob() {

    var index: Int
        get() = entity.index
        set(value) { entity.index = value }

    var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }
}
