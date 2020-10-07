package gg.rsmod.game.model.mob

import com.google.common.base.MoreObjects
import gg.rsmod.game.action.ActionBus
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.event.impl.LoginEvent
import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.message.ServerPacketListener
import gg.rsmod.game.model.client.NpcEntity
import gg.rsmod.game.model.client.PlayerEntity
import gg.rsmod.game.model.domain.Direction
import gg.rsmod.game.model.domain.PlayerId
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.map.Viewport
import gg.rsmod.game.model.step.StepQueue
import gg.rsmod.game.model.step.StepSpeed
import java.util.ArrayDeque
import java.util.Queue

sealed class Mob(
    val steps: StepQueue = StepQueue(),
    var speed: StepSpeed = StepSpeed.Walk,
    val movement: Queue<Direction> = ArrayDeque()
)

class Player(
    val id: PlayerId,
    val loginName: String,
    val entity: PlayerEntity,
    val eventBus: EventBus,
    val actionBus: ActionBus,
    val viewport: Viewport = Viewport(),
    private val messageListeners: List<ServerPacketListener> = mutableListOf()
) : Mob() {

    val username: String
        get() = entity.username

    var index: Int
        get() = entity.index
        set(value) { entity.index = value }

    var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }

    fun login() {
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.High))
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.Normal))
        eventBus.publish(LoginEvent(this, LoginEvent.Priority.Low))
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
