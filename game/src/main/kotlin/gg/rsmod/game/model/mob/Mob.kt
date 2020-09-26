package gg.rsmod.game.model.mob

import com.google.common.base.MoreObjects
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.event.impl.LoginEvent
import gg.rsmod.game.message.MessageListener
import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.model.client.NpcEntity
import gg.rsmod.game.model.client.PlayerEntity
import gg.rsmod.game.model.domain.PlayerId
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.map.Viewport

sealed class Mob

class Player(
    val id: PlayerId,
    val loginName: String,
    val entity: PlayerEntity,
    private val messageListeners: List<MessageListener>,
    val viewport: Viewport = Viewport()
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

    override fun toString(): String = MoreObjects
        .toStringHelper(this)
        .add("loginName", loginName)
        .add("displayName", username)
        .add("id", id)
        .add("index", index)
        .add("coords", coords)
        .toString()
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
